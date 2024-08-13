package com.gameon.api.server.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.EndpointHandlerData;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerModule extends AbstractModule {
    private IPlayerExtension playerExtension;

    @Override
    public Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        playerExtension = (IPlayerExtension) extension;
        Set<EndpointHandlerData> endpoints = new HashSet<>();

        endpoints.add(new EndpointHandlerData(
                "player/{uuid}",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                this::getPlayer
        ));

        endpoints.add(new EndpointHandlerData(
                "player",
                HandlerType.GET,
                HandlerAccessType.AUTHORIZED,
                this::getPlayer
        ));

        return endpoints;
    }

    private void getPlayer(Context ctx, UserId userId) {
        success(ctx, Map.of(
                "player", playerExtension.getPlayer(userId)
        ));
    }


    @Override
    public String getDefaultPath() {
        return "player";
    }
}
