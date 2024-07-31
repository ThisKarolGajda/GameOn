package com.gameon.api.server;

import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.IModuleInfo;
import com.gameon.api.server.extension.ModuleInfoFactory;
import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.rest.IRestServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public interface IGameOnApiServer {

    List<GameOnFeatureType> enabledFeatures();

    default List<IModuleInfo> getActiveFeatures() {
        List<IModuleInfo> features = new ArrayList<>();
        for (GameOnFeatureType feature : enabledFeatures()) {
            features.add(ModuleInfoFactory.create(feature));
        }
        return features;
    }

    GameServerType getServerType();

    Logger getLogger();

    IRestServer getRestServer();

    default boolean isFeatureEnabled(GameOnFeatureType gameOnFeatureType) {
        return enabledFeatures().contains(gameOnFeatureType);
    }

    <E extends IExtension> @Nullable E getExtension(GameOnFeatureType gameOnFeatureType);
}
