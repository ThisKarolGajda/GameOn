package com.gameon.api.server.features.stats;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.ContextHandler;
import com.gameon.api.server.extension.handler.EndpointHandlerData;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatsModule extends AbstractModule {
    private IStatsExtension statsExtension;

    @Override
    public Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        statsExtension = (IStatsExtension) extension;
        Set<EndpointHandlerData> endpoints = new HashSet<>();

        endpoints.add(new EndpointHandlerData(
                "global",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                this::getGlobalStats
        ));

        endpoints.add(new EndpointHandlerData(
                "user",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                this::getUser,
                this::getUserStats
        ));

        endpoints.add(new EndpointHandlerData(
                "user/{uuid}",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                this::getUser,
                this::getUserStats
        ));

        return endpoints;
    }

    private UserId getUser(@NotNull Context ctx) {
        Map<String, String> pathParamMap = ctx.pathParamMap();
        if (pathParamMap.containsKey("uuid")) {
            String uuid = pathParamMap.get("uuid");
            return UserId.fromUuidString(uuid);
        }

        return ContextHandler.authenticateUser(GameOnInstance.getFeatureRegistrar().getExtension("AUTHENTICATION"), ctx).orElse(null);
    }

    private void getUserStats(Context ctx, UserId userId) {
        if (userId == null) {
            return;
        }

        Map<String, Long> values = new HashMap<>();
        for (StatType type : StatType.values()) {
            values.put(type.name(), statsExtension.getValue(type, userId));
        }

        success(ctx, Map.of(
                "values", values
        ));
    }

    private void getGlobalStats(Context ctx) {
        Map<String, Long> values = new HashMap<>();
        for (StatType type : StatType.values()) {
            long valueSummed = statsExtension.getValueSummed(type);
            values.put(type.name(), valueSummed);
        }

        success(ctx, Map.of(
                "values", values
        ));
    }

    @Override
    public String getDefaultPath() {
        return "stats";
    }
}
