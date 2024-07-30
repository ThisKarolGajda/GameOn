package com.gameon.api.server.rest;

import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IModuleInfo;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerContextHandler;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.features.authentication.IAuthentication;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.router.Endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JavalinRestServer implements IRestServer {
    private static final String DEFAULT_PATH = "/api/v1";
    private RestServerSettings settings;
    private Javalin app;
    private IGameOnApiServer apiServer;
    private List<String> activeEndpoints;
    private IAuthentication authentication;

    @Override
    public void initialize(RestServerSettings settings, IGameOnApiServer apiServer) {
        this.settings = settings;
        this.apiServer = apiServer;
        this.authentication = apiServer.getExtension(GameOnFeatureType.AUTHENTICATION);
        app = Javalin.create().start(settings.port());
        activeEndpoints = new ArrayList<>();

        apiServer.getLogger().info("Loading features...");
        for (IModuleInfo feature : apiServer.getActiveFeatures()) {
            enableFeature(feature);
        }

        app.get("/", ctx -> ctx.json(activeEndpoints));

        app.exception(Exception.class, (e, ctx) ->
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage())
        );

        apiServer.getLogger().info("Javalin server started on port " + settings.port());
    }

    private void enableFeature(IModuleInfo feature) {
        apiServer.getLogger().info("Enabling feature: " + feature.getDefaultPath() + " (" + feature + ")");
        feature.getRoutes(settings.features().get(feature.getType())).forEach((handler) -> {
            String path = DEFAULT_PATH + "/" + feature.getDefaultPath() + "/" + handler.getPath();
            if (handler.getAccessType() != HandlerAccessType.EVERYONE) {
                app.before(path, ctx -> handleAuthorization(ctx, handler));
            }
            app.addEndpoint(new Endpoint(handler.getHandlerType(), path, ctx -> {
                try {
                    HandlerContextHandler.handle(handler, ctx);
                } catch (Exception exception) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
                    ctx.json(Map.of("error", "An unexpected error occurred: " + exception.getMessage()));
                }
            }));
            activeEndpoints.add(path);
        });
    }

    private void handleAuthorization(Context ctx, HandlerData handler) {
        if (!hasPermission(ctx, handler)) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Unauthorized access");
            ctx.skipRemainingHandlers();
        }
    }

    private boolean hasPermission(Context ctx, HandlerData handler) {
        if (authentication == null
                || !apiServer.isFeatureEnabled(GameOnFeatureType.AUTHENTICATION)
                || handler.getAccessType() == HandlerAccessType.EVERYONE) {
            return true;
        }

        Optional<UserId> userIdOptional = authenticateUser(ctx);
        if (userIdOptional.isEmpty()) {
            return false;
        }

        UserId userId = userIdOptional.get();
        if (handler.getAccessType() == HandlerAccessType.AUTHORIZED) {
            return true;
        }

        if (handler.getAccessType() == HandlerAccessType.ADMIN) {
            return isAdmin(userId);
        }

        UserId userId1 = HandlerContextHandler.getUserIdFromContext(handler, ctx);
        return userId.equals(userId1);
    }

    private Optional<UserId> authenticateUser(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (authentication != null && authentication.validateToken(token)) {
                return Optional.of(authentication.getUserFromToken(token));
            }
        }
        return Optional.empty();
    }

    private boolean isAdmin(UserId userId) {
        //todo
        return false;
    }

    @Override
    public void stop(RestServerStopReasonType stopReason) {
        if (app != null) {
            app.stop();
            apiServer.getLogger().info("Javalin server stopped. Reason: " + stopReason);
        }
    }
}
