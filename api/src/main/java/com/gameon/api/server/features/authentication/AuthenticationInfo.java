package com.gameon.api.server.features.authentication;

import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.IModuleInfo;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;

import java.util.Set;

public class AuthenticationInfo implements IModuleInfo {
    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.AUTHENTICATION;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        return Set.of();
    }

    @Override
    public String getDefaultPath() {
        return "authentication";
    }
}
