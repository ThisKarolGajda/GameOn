package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HandlerContextHandler {
    public static void handle(HandlerData handler, Context ctx) {
        System.out.println("1 - HandlerContextHandler");
        if (handler.getContextOwnerConsumer() != null) {
            System.out.println(ctx + " --- " + handler.getContextOwnerConsumer());
            UserId ownerId = handler.getOwnerIdSupplier() != null ? handler.getOwnerIdSupplier().apply(ctx) : null;
            handler.getContextOwnerConsumer().accept(ctx, ownerId);
            return;
        }

        System.out.println("2 - HandlerContextHandler");

        if (!ctx.pathParamMap().isEmpty()) {
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
        System.out.println("3 - HandlerContextHandler");

        if (handler.getContextConsumer() != null) {
            handler.getContextConsumer().accept(ctx);
            return;
        }

        System.out.println("4 - HandlerContextHandler");
        ctx.status(500).json(Map.of("message", "Not found", "success", false));
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
}
