package com.gameon.api.server.extension.handler;

import com.gameon.api.server.common.UserId;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HandlerContextHandlerTest {
    private Context ctx;
    private HandlerData handlerData;

    @BeforeEach
    public void setUp() {
        ctx = mock(Context.class);
        handlerData = mock(HandlerData.class);
    }

    @Test
    public void testHandleWithContextConsumer() {
        when(ctx.pathParam("uuid")).thenReturn("");

        Consumer<Context> contextConsumer = context -> {
            when(context.status(200)).thenReturn(context);
            when(context.result("Handled context consumer")).thenReturn(context);
            context.status(200);
            context.result("Handled context consumer");
        };

        when(handlerData.getContextConsumer()).thenReturn(contextConsumer);

        HandlerContextHandler.handle(handlerData, ctx);

        verify(ctx).status(200);
        verify(ctx).result("Handled context consumer");
    }

    @Test
    public void testHandleWithNoConsumers() {
        when(ctx.pathParam("uuid")).thenReturn("");
        when(handlerData.getContextConsumer()).thenReturn(null);
        when(handlerData.getContextOwnerConsumer()).thenReturn(null);

        when(ctx.status(500)).thenReturn(ctx);
        when(ctx.result("No handler available for this route.")).thenReturn(ctx);

        HandlerContextHandler.handle(handlerData, ctx);

        verify(ctx).status(500);
        verify(ctx).result("No handler available for this route.");
    }

    @Test
    public void testHandleWithOwnerConsumerAndEmptyUuid() {
        when(ctx.pathParam("uuid")).thenReturn("");

        BiConsumer<Context, UserId> ownerConsumer = (context, id) -> {
            assertNull(id);
            when(context.status(200)).thenReturn(context);
            when(context.result("Handled owner consumer")).thenReturn(context);
            context.status(200);
            context.result("Handled owner consumer");
        };

        when(handlerData.getContextOwnerConsumer()).thenReturn(ownerConsumer);

        HandlerContextHandler.handle(handlerData, ctx);

        verify(ctx).status(200);
        verify(ctx).result("Handled owner consumer");
    }

    @Test
    public void testGetUserIdFromContextWithOwnerIdSupplierAndEmptyUuid() {
        when(ctx.pathParam("uuid")).thenReturn("");
        Function<Context, UserId> ownerIdSupplier = context -> UserId.fromUuid("123e4567-e89b-12d3-a456-426614174000");
        when(handlerData.getOwnerIdSupplier()).thenReturn(ownerIdSupplier);

        UserId userId = HandlerContextHandler.getUserIdFromContext(handlerData, ctx);

        assertNull(userId);
    }

    @Test
    public void testGetUserIdFromContextWithEmptyUuidAndNoOwnerIdSupplier() {
        when(ctx.pathParam("uuid")).thenReturn("");
        when(handlerData.getOwnerIdSupplier()).thenReturn(null);

        UserId userId = HandlerContextHandler.getUserIdFromContext(handlerData, ctx);

        assertNull(userId);
    }

    @Test
    public void testHandleWithContextConsumerAndValidUuid() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        when(ctx.pathParam("uuid")).thenReturn("uuid");

        Consumer<Context> contextConsumer = context -> {
            when(context.status(200)).thenReturn(context);
            when(context.result("Handled context consumer")).thenReturn(context);
            context.status(200);
            context.result("Handled context consumer");
        };

        when(handlerData.getContextConsumer()).thenReturn(contextConsumer);

        HandlerContextHandler.handle(handlerData, ctx);

        verify(ctx).status(200);
        verify(ctx).result("Handled context consumer");
    }
}
