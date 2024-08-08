package com.gameon.api.server.rest;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerContextHandler;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.authentication.ITokenAuthenticationExtension;
import com.gameon.api.server.features.permission.IPermissionExtension;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.router.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class JavalinRestServer implements IRestServer {
    private static final String DEFAULT_PATH = "/api/v1";
    private Javalin app;
    private IGameOnApiServer apiServer;
    private Map<String, HandlerType> activeEndpoints;
    private ITokenAuthenticationExtension authentication;

    @Override
    public void initialize(RestServerSettings settings, IGameOnApiServer apiServer) {
        this.apiServer = apiServer;
        this.authentication = apiServer.getFeatureRegistrar().getExtension("AUTHENTICATION");
        app = Javalin.create(config -> config.bundledPlugins.enableCors(cors -> cors.addRule(it -> {
            it.anyHost();
            it.allowCredentials = true;
        }))).start(settings.port());

        activeEndpoints = new LinkedHashMap<>();

        apiServer.getLogger().info("Loading features...");
        for (Map.Entry<String, ? extends IExtension> feature : apiServer.getFeatureRegistrar().getFeatures().entrySet()) {
            enableFeature(feature.getKey(), feature.getValue());
        }

        if (GameOnInstance.getRegistry().getSettingValue("REST_DISPLAY_ROUTES")) {
            app.get(DEFAULT_PATH + "/routes", ctx -> ctx.json(Map.of(
                    "routes", activeEndpoints
            )));
        }

        if (GameOnInstance.getRegistry().getSettingValue("REST_DISPLAY_FEATURES")) {
            app.get(DEFAULT_PATH + "/features", ctx -> ctx.json(Map.of(
                    "features", apiServer.getFeatureRegistrar().getFeaturesList()
            )));
        }

        app.exception(Exception.class, (e, ctx) ->
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                        "success", false,
                        "error", e.getMessage()
                ))
        );

        apiServer.getLogger().info("Javalin server started on port " + settings.port());
    }

    public void enableFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Enabling feature: " + featureName + " (" + feature + ")");
        feature.getRoutes(extension).forEach(handler -> {
            String path = getPath(handler, feature);

            if (handler.getAccessType() != HandlerAccessType.EVERYONE) {
                app.before(path, ctx -> handleAuthorization(ctx, handler));
            }

            app.addEndpoint(new Endpoint(handler.getHandlerType(), path, ctx -> ctx.future(() -> HandlerContextHandler.handleAsync(authentication, handler, ctx))));

            activeEndpoints.put(path, handler.getHandlerType());
        });
    }

    public void disableFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Disabling feature: " + featureName + " (" + feature + ")");
        feature.getRoutes(extension).forEach(handler -> {
            String path = getPath(handler, feature);

            if (handler.getAccessType() != HandlerAccessType.EVERYONE) {
                app.before(path, ctx -> ctx.status(404));
            }

            app.addEndpoint(new Endpoint(handler.getHandlerType(), path, ctx -> ctx.status(404)));

            activeEndpoints.remove(path);
        });
    }

    private static @NotNull String getPath(@NotNull HandlerData handler, @NotNull AbstractModule feature) {
        return DEFAULT_PATH + "/" + feature.getDefaultPath() + "/" + handler.getPath();
    }

    private void handleAuthorization(Context ctx, HandlerData handler) {
        if (!hasPermission(ctx, handler)) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", "Unauthorized access", "success", false));
            ctx.skipRemainingHandlers();
        }
    }

    private boolean hasPermission(Context ctx, HandlerData handler) {
        if (authentication == null
                || !apiServer.isFeatureEnabled("AUTHENTICATION")
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
            if (authentication.validateToken(token)) {
                return Optional.of(authentication.getUserFromToken(token));
            }
        }
        return Optional.empty();
    }

    private boolean isAdmin(UserId userId) {
        IPermissionExtension permission = apiServer.getFeatureRegistrar().getExtension("PERMISSION");
        return permission != null && permission.isAdmin(userId);
    }

    @Override
    public void stop(RestServerStopReasonType stopReason) {
        if (app != null) {
            app.stop();
            apiServer.getLogger().info("Javalin server stopped. Reason: " + stopReason);
        }
    }
}
