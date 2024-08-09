package com.gameon.api.server.server;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.*;
import com.gameon.api.server.features.authentication.ITokenAuthenticationExtension;
import com.gameon.api.server.features.permission.IPermissionExtension;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.router.Endpoint;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class JavalinServer implements IServer {
    private static final String DEFAULT_PATH = "/api/v1";
    private Javalin app;
    private IGameOnApiServer apiServer;
    private Map<String, HandlerType> activeRestEndpoints;
    private Map<String, WebSocketHandlerData> activeWebSocketHandlers;
    private ITokenAuthenticationExtension authentication;

    @Override
    public void initialize(ServerSettings settings, IGameOnApiServer apiServer) {
        this.apiServer = apiServer;
        this.authentication = apiServer.getFeatureRegistrar().getExtension("AUTHENTICATION");

        app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> {
                it.anyHost();
                it.allowCredentials = true;
            }));

            config.jetty.modifyWebSocketServletFactory(ws -> {
                ws.setIdleTimeout(Duration.of(5, ChronoUnit.MINUTES));
            });
        }).start(settings.port());

        activeRestEndpoints = new LinkedHashMap<>();
        activeWebSocketHandlers = new LinkedHashMap<>();

        apiServer.getLogger().info("Loading features...");
        for (Map.Entry<String, ? extends IExtension> feature : apiServer.getFeatureRegistrar().getFeatures().entrySet()) {
            enableRestFeature(feature.getKey(), feature.getValue());
            enableWebSocketFeature(feature.getKey(), feature.getValue());
        }

        if (GameOnInstance.getRegistry().getSettingValue("REST_DISPLAY_ROUTES")) {
            app.get(DEFAULT_PATH + "/routes", ctx -> ctx.json(Map.of("routes", activeRestEndpoints)));
        }

        if (GameOnInstance.getRegistry().getSettingValue("REST_DISPLAY_FEATURES")) {
            app.get(DEFAULT_PATH + "/features", ctx -> ctx.json(Map.of("features", apiServer.getFeatureRegistrar().getFeaturesList())));
        }

        app.exception(Exception.class, (e, ctx) -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("success", false, "error", e.getMessage())));

        apiServer.getLogger().info("Javalin server started on port " + settings.port());
    }

    public void enableRestFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Enabling REST feature: " + featureName + " (" + feature + ")");
        feature.getEndpoints(extension).forEach(handler -> {
            String path = getPath(handler, feature);

            if (handler.getAccessType() != HandlerAccessType.EVERYONE) {
                app.before(path, ctx -> handleAuthorization(ctx, handler));
            }

            app.addEndpoint(new Endpoint(handler.getHandlerType(), path, ctx -> ctx.future(() -> ContextHandler.handleAsync(authentication, handler, ctx))));

            activeRestEndpoints.put(path, handler.getHandlerType());
        });
    }

    public void disableRestFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Disabling REST feature: " + featureName + " (" + feature + ")");
        feature.getEndpoints(extension).forEach(handler -> {
            String path = getPath(handler, feature);
            app.before(path, ctx -> ctx.status(404));
            app.addEndpoint(new Endpoint(handler.getHandlerType(), path, ctx -> ctx.status(404)));
            activeRestEndpoints.remove(path);
        });
    }

    public void enableWebSocketFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Enabling WebSocket feature: " + featureName + " (" + feature + ")");
        feature.getWebSockets(extension).forEach(handler -> {
            String path = getPath(handler, feature);
            app.ws(path, ws -> {
                ws.onConnect(ctx -> handleWebSocketConnect(featureName, ctx, handler));
                ws.onMessage(ctx -> handleWebSocketMessage(ctx, handler));
                ws.onClose(ctx -> handleWebSocketClose(featureName, ctx, handler));
                ws.onError(ctx -> handleWebSocketError(ctx, handler));
            });
            activeWebSocketHandlers.put(path, handler);
        });
    }

    public void disableWebSocketFeature(String featureName, IExtension extension) {
        AbstractModule feature = apiServer.getFeatureRegistrar().getModule(featureName);
        apiServer.getLogger().info("Disabling WebSocket feature: " + featureName + " (" + feature + ")");
        feature.getWebSockets(extension).forEach(handler -> {
            String path = getPath(handler, feature);
            app.ws(path, ws -> {
                ws.onConnect(WsContext::closeSession);
                ws.onMessage(WsContext::closeSession);
                ws.onClose(ctx -> {
                });
                ws.onError(ctx -> {
                });
            });
            activeWebSocketHandlers.remove(path);
        });
    }

    private static @NotNull String getPath(@NotNull IHandlerData handler, @NotNull AbstractModule feature) {
        return DEFAULT_PATH + "/" + feature.getDefaultPath() + "/" + handler.getPath();
    }

    private void handleAuthorization(Context ctx, EndpointHandlerData handler) {
        if (!hasPermission(ctx, handler)) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", "Unauthorized access", "success", false));
            ctx.skipRemainingHandlers();
        }
    }

    private boolean hasPermission(Context ctx, EndpointHandlerData handler) {
        if (authentication == null || !apiServer.isFeatureEnabled("AUTHENTICATION") || handler.getAccessType() == HandlerAccessType.EVERYONE) {
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

        UserId userId1 = ContextHandler.getUserIdFromContext(handler, ctx);
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

    private void handleWebSocketConnect(String featureName, WsConnectContext ctx, WebSocketHandlerData handler) {
        AbstractModule module = apiServer.getFeatureRegistrar().getModule(featureName);
        module.addClient(ctx);
    }

    private void handleWebSocketMessage(WsMessageContext ctx, WebSocketHandlerData handler) {
        ContextHandler.handle(authentication, handler, ctx);
    }

    private void handleWebSocketClose(String featureName, WsCloseContext ctx, WebSocketHandlerData handler) {
        AbstractModule module = apiServer.getFeatureRegistrar().getModule(featureName);
        module.removeClient(ctx);
    }

    private void handleWebSocketError(WsErrorContext ctx, WebSocketHandlerData handler) {
        // Handle WebSocket error logic here
        System.err.println("Error on path: " + handler.getPath() + ", Error: " + ctx.error().getMessage());
    }

    @Override
    public void stop(ServerStopReasonType stopReason) {
        if (app != null) {
            app.stop();
            apiServer.getLogger().info("Javalin server stopped. Reason: " + stopReason);
        }
    }
}
