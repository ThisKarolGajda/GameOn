package com.gameon.api.server.features;

import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.extension.IExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FeatureRegistrar {
    private final Logger logger;
    private final Map<GameOnFeatureType, IExtension> features;

    public FeatureRegistrar(IGameOnApiServer apiServer) {
        this.logger = apiServer.getLogger();
        this.features = new HashMap<>();
    }

    public <E extends IExtension> void registerExtension(GameOnFeatureType feature, @NotNull E extension) {
        if (features.containsKey(feature)) {
            logger.info("Extension type of " + extension.getClass().getSimpleName() + " (" + feature.name() + ") already registered");
            return;
        }

        if (extension.canBeUsed()) {
            features.put(feature, extension);
            logger.info("Registering extension " + extension.getClass().getSimpleName());
        } else {
            logger.warning("Extension " + extension.getClass().getSimpleName() + " cannot be registered");
        }
    }

    public List<GameOnFeatureType> getEnabledFeatures() {
        return List.copyOf(features.keySet());
    }

    public Map<GameOnFeatureType, IExtension> getFeatures() {
        return features;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <E extends IExtension> E getExtension(GameOnFeatureType gameOnFeatureType) {
        IExtension extension = features.get(gameOnFeatureType);
        if (extension != null) {
            return (E) extension;
        }
        return null;
    }

    public void dispose() {
        features.clear();
    }
}
