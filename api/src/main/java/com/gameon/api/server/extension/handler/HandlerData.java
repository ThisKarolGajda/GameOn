package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandlerData {
    private final String path;
    private final HandlerType handlerType;
    private final HandlerAccessType accessType;
    private final Function<Context, UserId> ownerIdSupplier;
    private final Consumer<Context> contextConsumer;
    private final BiConsumer<Context, UserId> contextOwnerConsumer;

    public HandlerData(String path, HandlerType handlerType, HandlerAccessType accessType, Function<Context, UserId> ownerIdSupplier, Consumer<Context> contextConsumer, BiConsumer<Context, UserId> contextOwnerConsumer) {
        this.path = path;
        this.handlerType = handlerType;
        this.accessType = accessType;
        this.ownerIdSupplier = ownerIdSupplier;
        this.contextConsumer = contextConsumer;
        this.contextOwnerConsumer = contextOwnerConsumer;
    }

    public HandlerData(String path, HandlerType handlerType, Function<Context, UserId> ownerIdSupplier, Consumer<Context> contextConsumer) {
        this(path, handlerType, determineAccessType(path), ownerIdSupplier, contextConsumer, null);
    }

    public HandlerData(String path, HandlerType handlerType, HandlerAccessType accessType, Function<Context, UserId> ownerIdSupplier, Consumer<Context> contextConsumer) {
        this(path, handlerType, accessType, ownerIdSupplier, contextConsumer, null);
    }

    public HandlerData(String path, HandlerType handlerType, HandlerAccessType type, Consumer<Context> contextConsumer) {
        this(path, handlerType, type, null, contextConsumer, null);
    }

    public HandlerData(String path, HandlerType handlerType, Consumer<Context> contextConsumer) {
        this(path, handlerType, (Function<Context, UserId>) null, contextConsumer);
    }

    public HandlerData(String path, HandlerType handlerType, Function<Context, UserId> ownerIdSupplier, BiConsumer<Context, UserId> contextOwnerConsumer) {
        this(path, handlerType, determineAccessType(path), ownerIdSupplier, null, contextOwnerConsumer);
    }

    public HandlerData(String path, HandlerType handlerType, HandlerAccessType accessType, Function<Context, UserId> ownerIdSupplier, BiConsumer<Context, UserId> contextOwnerConsumer) {
        this(path, handlerType, accessType, ownerIdSupplier, null, contextOwnerConsumer);
    }

    public HandlerData(String path, HandlerType handlerType, HandlerAccessType type, BiConsumer<Context, UserId> contextOwnerConsumer) {
        this(path, handlerType, type, null, null, contextOwnerConsumer);
    }

    public HandlerData(String path, HandlerType handlerType, BiConsumer<Context, UserId> contextOwnerConsumer) {
        this(path, handlerType, (Function<Context, UserId>) null, contextOwnerConsumer);
    }

    @Contract(pure = true)
    private static HandlerAccessType determineAccessType(@NotNull String path) {
        return path.contains("{uuid}") ? HandlerAccessType.OWNER : HandlerAccessType.AUTHORIZED;
    }

    public String getPath() {
        return path;
    }

    public HandlerType getHandlerType() {
        return handlerType;
    }

    public HandlerAccessType getAccessType() {
        return accessType;
    }

    public Function<Context, UserId> getOwnerIdSupplier() {
        return ownerIdSupplier;
    }

    public BiConsumer<Context, UserId> getContextOwnerConsumer() {
        return contextOwnerConsumer;
    }

    public Consumer<Context> getContextConsumer() {
        return contextConsumer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandlerData that)) return false;
        return Objects.equals(path, that.path) && handlerType == that.handlerType && accessType == that.accessType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, handlerType, accessType);
    }
}
