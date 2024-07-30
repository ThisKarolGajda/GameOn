package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

public class HandlerContextHandler {
    public static void handle(HandlerData handler, Context ctx) {
        if (handler.getContextOwnerConsumer() != null) {
            UserId ownerId = handler.getOwnerIdSupplier() != null ? handler.getOwnerIdSupplier().apply(ctx) : null;
            handler.getContextOwnerConsumer().accept(ctx, ownerId);
            return;
        }

        if (!ctx.pathParamMap().isEmpty()) {
            String uuid = ctx.pathParam("uuid");
            if (!uuid.isEmpty()) {
                if (handler.getContextOwnerConsumer() != null) {
                    handler.getContextOwnerConsumer().accept(ctx, UserId.fromUuid(uuid));
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

        ctx.status(500).result("No handler available for this route.");
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
                return UserId.fromUuid(uuid);
            }
        }

        return null;
    }
}
