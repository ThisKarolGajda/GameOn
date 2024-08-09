package com.gameon.api.server.features.dailyreward;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.EndpointHandlerData;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DailyRewardModule extends AbstractModule {
    private IDailyRewardExtension dailyRewardExtension;

    @Override
    public Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        dailyRewardExtension = (IDailyRewardExtension) extension;
        Set<EndpointHandlerData> routes = new HashSet<>();

        routes.add(new EndpointHandlerData(
                "has",
                HandlerType.GET,
                HandlerAccessType.AUTHORIZED,
                this::hasDailyReward
        ));

        routes.add(new EndpointHandlerData(
                "claim",
                HandlerType.GET,
                HandlerAccessType.AUTHORIZED,
                this::claimDailyReward
        ));

        return routes;
    }

    private void hasDailyReward(Context ctx, UserId userId) {
        if (userId == null) {
            error(ctx, "Couldn't access userId");
            return;
        }

        success(ctx, Map.of(
                "value", dailyRewardExtension.hasClaimed(userId)
        ));
    }

    private void claimDailyReward(Context ctx, UserId userId) {
        if (userId == null) {
            error(ctx, "Couldn't access userId");
            return;
        }

        success(ctx, Map.of(
                "claimed", dailyRewardExtension.claim(userId)
        ));
    }

    @Override
    public String getDefaultPath() {
        return "daily-reward";
    }
}
