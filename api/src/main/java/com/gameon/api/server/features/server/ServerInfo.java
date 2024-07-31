package com.gameon.api.server.features.server;

import com.gameon.api.server.extension.AbstractModuleInfo;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;

import java.util.HashSet;
import java.util.Set;

public class ServerInfo extends AbstractModuleInfo {
    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.SERVER;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        Set<HandlerData> routes = new HashSet<>();
        IServer server = (IServer) extension;



        return routes;
    }

    @Override
    public String getDefaultPath() {
        return "server";
    }
}
