package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.authentication.ITokenAuthenticationExtension;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.gameon.api.server.extension.AbstractModule.error;

public class HandlerContextHandler {
    public static CompletableFuture<Void> handleAsync(ITokenAuthenticationExtension authenticationInfo, HandlerData handler, Context ctx) {
        return CompletableFuture.runAsync(() -> {
            if (handler.getContextOwnerConsumer() != null) {
                UserId ownerId = handler.getOwnerIdSupplier() != null ? handler.getOwnerIdSupplier().apply(ctx) : null;
                if (ownerId == null) {
                    Optional<UserId> optional = authenticateUser(authenticationInfo, ctx);
                    if (optional.isEmpty()) {
                        return;
                    }

                    ownerId = optional.get();
                }

                handler.getContextOwnerConsumer().accept(ctx, ownerId);
                return;
            }

            if (!ctx.pathParamMap().isEmpty() && ctx.pathParamMap().containsKey("uuid")) {
                String uuid = ctx.pathParam("uuid");
                if (!uuid.isEmpty()) {
                    if (handler.getContextOwnerConsumer() != null) {
                        handler.getContextOwnerConsumer().accept(ctx, UserId.fromUuidString(uuid));
                    } else {
                        handler.getContextConsumer().accept(ctx);
                    }
                    return;
                }
            }

            if (handler.getContextConsumer() != null) {
                handler.getContextConsumer().accept(ctx);
                return;
            }

            ctx.status(500).json(Map.of("message", "Not found", "success", false));
        });
    }

    @Nullable
    public static UserId getUserIdFromContext(HandlerData handler, Context ctx) {
        if (ctx == null) {
            return null;
        }

        if (handler != null) {
            if (handler.getContextOwnerConsumer() != null) {
                return handler.getOwnerIdSupplier() != null ? handler.getOwnerIdSupplier().apply(ctx) : null;
            }
        }

        if (!ctx.pathParamMap().isEmpty()) {
            String uuid = ctx.pathParam("uuid");
            if (!uuid.isEmpty()) {
                return UserId.fromUuidString(uuid);
            }
        }

        return null;
    }

    private static Optional<UserId> authenticateUser(ITokenAuthenticationExtension authentication, @NotNull Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (authentication.validateToken(token)) {
                return Optional.of(authentication.getUserFromToken(token));
            } else {
                error(ctx, "Expired token");
            }
        } else {
            error(ctx, "Invalid token");
        }

        return Optional.empty();
    }

}