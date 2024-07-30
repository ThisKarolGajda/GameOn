package com.gameon.api.server.extension;

import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;

import java.util.Set;

public interface IModuleInfo {

    GameOnFeatureType getType();

    Set<HandlerData> getRoutes(IExtension extension);

    String getDefaultPath();
}
