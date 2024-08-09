package com.gameon.api.server.adminsettings;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.EndpointHandlerData;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdminSettingsModule extends AbstractModule {
    @Override
    public Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        Set<EndpointHandlerData> routes = new HashSet<>();

        routes.add(new EndpointHandlerData(
                "type/{key}",
                HandlerType.GET,
                HandlerAccessType.ADMIN,
                this::getType
        ));

        routes.add(new EndpointHandlerData(
                "value/{key}",
                HandlerType.GET,
                HandlerAccessType.ADMIN,
                this::getValue
        ));

        routes.add(new EndpointHandlerData(
                "value/{key}",
                HandlerType.POST,
                HandlerAccessType.ADMIN,
                this::setValue
        ));

        return routes;
    }

    private void setValue(Context ctx) {
        AdminSettingsRegistry registry = GameOnInstance.getRegistry();
        AdminSettingsRegistry.AdminRegistryValue setting = registry.getSetting(ctx.pathParam("key"));

        if (setting == null) {
            error(ctx, "Setting not found");
            return;
        }

        Map<String, Object> json = deserialize(ctx);
        if (json == null) return;

        Object value = json.get("value");
        Class<?> expectedClass = setting.getExpectedObjectClass();

        if (value != null && isValidType(expectedClass, value)) {
            setting.setValue(convertValue(expectedClass, value));
            success(ctx, "Changed setting " + setting.getKey() + " to " + value);
        } else {
            error(ctx, "Invalid type for setting: expected " + expectedClass.getSimpleName()
                    + " but got " + (value != null ? value.getClass().getSimpleName() : "null"));
        }
    }

    private boolean isValidType(Class<?> expectedClass, Object value) {
        return (expectedClass.isPrimitive() &&
                ((expectedClass == boolean.class && value instanceof Boolean) ||
                        (value instanceof Number)))
                || (expectedClass == char.class && value instanceof String && ((String) value).length() == 1)
                || expectedClass.isInstance(value);
    }

    private Object convertValue(Class<?> expectedClass, Object value) {
        if (expectedClass == boolean.class) return value;
        if (expectedClass == char.class) return ((String) value).charAt(0);
        if (value instanceof Number num) {
            if (expectedClass == byte.class) return num.byteValue();
            if (expectedClass == short.class) return num.shortValue();
            if (expectedClass == int.class) return num.intValue();
            if (expectedClass == long.class) return num.longValue();
            if (expectedClass == float.class) return num.floatValue();
            if (expectedClass == double.class) return num.doubleValue();
        }
        return value;
    }

    private void getType(Context ctx) {
        AdminSettingsRegistry registry = GameOnInstance.getRegistry();
        AdminSettingsRegistry.AdminRegistryValue setting = registry.getSetting(ctx.pathParam("key"));
        if (setting == null) {
            error(ctx, "Setting is not found");
            return;
        }

        Class<?> clazz = setting.getExpectedObjectClass();
        if (clazz == null) {
            error(ctx, "Type is null");
            return;
        }

        success(ctx, Map.of("type", clazz.getSimpleName()));
    }

    private void getValue(Context ctx) {
        AdminSettingsRegistry registry = GameOnInstance.getRegistry();
        AdminSettingsRegistry.AdminRegistryValue setting = registry.getSetting(ctx.pathParam("key"));
        if (setting == null) {
            error(ctx, "Setting is not found");
            return;
        }

        Object object = setting.getValue();
        if (object == null) {
            error(ctx, "Type is null");
            return;
        }

        success(ctx, Map.of("value", object));
    }

    @Override
    public String getDefaultPath() {
        return "admin-settings";
    }
}
