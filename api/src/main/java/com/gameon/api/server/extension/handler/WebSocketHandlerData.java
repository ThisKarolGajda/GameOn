package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import io.javalin.websocket.WsMessageContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WebSocketHandlerData implements IHandlerData {
    private final String path;
    private final HandlerAccessType accessType;
    private final Function<WsMessageContext, UserId> ownerIdSupplier;
    private final BiConsumer<WsMessageContext, UserId> messageOwnerConsumer;

    public WebSocketHandlerData(String path, HandlerAccessType accessType, Function<WsMessageContext, UserId> ownerIdSupplier, BiConsumer<WsMessageContext, UserId> messageOwnerConsumer) {
        this.path = path;
        this.accessType = accessType;
        this.ownerIdSupplier = ownerIdSupplier;
        this.messageOwnerConsumer = messageOwnerConsumer;
    }

    public WebSocketHandlerData(String path, HandlerAccessType accessType) {
        this(path, accessType, null, null);
    }

    public WebSocketHandlerData(String path, HandlerAccessType accessType, BiConsumer<WsMessageContext, UserId> messageOwnerConsumer) {
        this(path, accessType, null, messageOwnerConsumer);
    }

    @Contract(pure = true)
    private static HandlerAccessType determineAccessType(@NotNull String path) {
        return path.contains("{uuid}") ? HandlerAccessType.OWNER : HandlerAccessType.AUTHORIZED;
    }

    public String getPath() {
        return path;
    }

    public HandlerAccessType getAccessType() {
        return accessType;
    }

    public Function<WsMessageContext, UserId> getOwnerIdSupplier() {
        return ownerIdSupplier;
    }

    public BiConsumer<WsMessageContext, UserId> getMessageOwnerConsumer() {
        return messageOwnerConsumer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSocketHandlerData that)) return false;
        return Objects.equals(path, that.path) && accessType == that.accessType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, accessType);
    }
}
