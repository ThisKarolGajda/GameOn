package com.gameon.api.server;

import com.gameon.api.server.features.FeatureRegistry;
import com.gameon.api.server.rest.IRestServer;

import java.io.File;
import java.util.logging.Logger;

public interface IGameOnApiServer {

    GameServerType getServerType();

    Logger getLogger();

    IRestServer getRestServer();

    FeatureRegistry getFeatureRegistrar();

    default boolean isFeatureEnabled(String feature) {
        return getFeatureRegistrar().getFeatures().containsKey(feature);
    }

    File getDataFolder();
}
