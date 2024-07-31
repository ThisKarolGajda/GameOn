package com.gameon.api.server.extension;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractModuleInfo implements IModuleInfo {
    private final Gson gson;

    public AbstractModuleInfo() {
        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

    public void error(@NotNull Context context, String message) {
        context.status(HttpStatus.BAD_REQUEST).json(getGson().toJson(Map.of(
                "error", message,
                "success", false
        )));
    }

    public void error(@NotNull Context context, @NotNull Map<String, Object> json) {
        Map<String, Object> modifiableJson = new HashMap<>(json);
        modifiableJson.put("success", false);
        context.status(HttpStatus.BAD_REQUEST).json(getGson().toJson(modifiableJson));
    }

    public void success(@NotNull Context context, String message) {
        context.status(HttpStatus.OK).json(getGson().toJson(Map.of(
                "success", true,
                "message", message
        )));
    }

    public void success(@NotNull Context context, @NotNull Map<String, Object> json) {
        Map<String, Object> modifiableJson = new HashMap<>(json);
        modifiableJson.put("success", true);
        context.status(HttpStatus.OK).json(getGson().toJson(modifiableJson));
    }

    /**
     * Generic method to deserialize JSON from the request body into a specified type.
     *
     * @param ctx   The Javalin context.
     * @param type  The type to deserialize into.
     * @param <T>   The type parameter.
     * @return The deserialized object or null if an error occurs.
     */
    public <T> T deserializeRequestBody(@NotNull Context ctx, Type type) {
        try {
            return getGson().fromJson(ctx.body(), type);
        } catch (JsonSyntaxException e) {
            error(ctx, "Invalid request body");
            return null;
        }
    }

    public <T> T deserializeAndCastRequestBody(@NotNull Context ctx) {
        try {
            return getGson().fromJson(ctx.body(), new TypeToken<T>() {}.getType());
        } catch (JsonSyntaxException e) {
            error(ctx, "Invalid request body");
            return null;
        }
    }
}