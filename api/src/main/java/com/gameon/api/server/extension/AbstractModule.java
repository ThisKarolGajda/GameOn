package com.gameon.api.server.extension;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractModule implements IModuleInfo {
    private static Gson gson;
    private final Set<WsContext> connectedClients = new HashSet<>();

    public AbstractModule() {
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                                ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime()
                )
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (localDate, type, jsonSerializationContext) ->
                                new JsonPrimitive(formatter.format(localDate)
                                ))
                .setPrettyPrinting()
                .create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static void error(@NotNull Context context, String message) {
        context.status(HttpStatus.INTERNAL_SERVER_ERROR).json(getGson().toJson(Map.of(
                "error", message,
                "success", false
        )));
    }

    public static void error(@NotNull Context context, @NotNull Map<String, ?> json) {
        Map<String, Object> modifiableJson = new HashMap<>(json);
        modifiableJson.put("success", false);
        context.status(HttpStatus.INTERNAL_SERVER_ERROR).json(getGson().toJson(modifiableJson));
    }

    public static void success(@NotNull Context context, String message) {
        context.status(HttpStatus.OK).json(getGson().toJson(Map.of(
                "success", true,
                "message", message
        )));
    }

    public static void success(@NotNull Context context, @NotNull Map<String, ?> json) {
        Map<String, Object> modifiableJson = new HashMap<>(json);
        modifiableJson.put("success", true);
        context.status(HttpStatus.OK).json(getGson().toJson(modifiableJson));
    }

    public static <T> @Nullable T deserialize(@NotNull Context ctx) {
        try {
            return getGson().fromJson(ctx.body(), new TypeToken<T>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            error(ctx, "Invalid request body");
            return null;
        }
    }

    public static <T> @Nullable T deserialize(@NotNull WsMessageContext ctx) {
        try {
            return getGson().fromJson(ctx.message(), new TypeToken<T>() {}.getType());
        } catch (JsonSyntaxException e) {
            error(ctx, "Invalid message format");
            return null;
        }
    }

    private static void error(WsMessageContext ctx, String message) {
        ctx.send("{\"error\": \"" + message + "\"}");
    }

    public void addClient(WsContext ctx) {
        connectedClients.add(ctx);
        System.out.println("Added client " + ctx + " to " + connectedClients.size() + " clients, for " + getDefaultPath());
    }

    public void removeClient(WsContext ctx) {
        connectedClients.remove(ctx);
        System.out.println("Removed client " + ctx + " from " + connectedClients.size() + " clients, for " + getDefaultPath());
    }

    public void broadcastWebSocketMessage(Object messageObject) {
        String jsonMessage = gson.toJson(messageObject);
        for (WsContext client : connectedClients) {
            client.send(jsonMessage);
        }
    }
}
