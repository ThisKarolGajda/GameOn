package com.gameon.api.server.features;

import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FeatureRegistry {
    private final Logger logger;
    private final Map<String, IExtension> features;
    private final Map<String, AbstractModule> featuresModules;

    public FeatureRegistry(IGameOnApiServer apiServer) {
        this.logger = apiServer.getLogger();
        this.features = new HashMap<>();
        this.featuresModules = new HashMap<>();
    }

    public <E extends IExtension> void registerExtension(String feature, E extension, @NotNull AbstractModule module) {
        if (features.containsKey(feature)) {
            logger.info("Feature, type of " + extension.getClass().getSimpleName() + ", " + feature + " is already registered");
            return;
        }

        if (extension == null || extension.canBeUsed()) {
            features.put(feature, extension);
            featuresModules.put(feature, module);
            logger.info("Registering feature " + feature + " " + (extension == null ? "" : extension.getClass().getSimpleName()));
        } else {
            logger.warning("Feature " + feature + " cannot be registered");
        }
    }

    public AbstractModule getModule(String feature) {
        return featuresModules.get(feature);
    }

    public List<String> getFeaturesList() {
        return new ArrayList<>(features.keySet());
    }

    public Map<String, IExtension> getFeatures() {
        return features;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <E extends IExtension> E getExtension(@NotNull String feature) {
        IExtension extension = features.get(feature.toUpperCase());
        if (extension != null) {
            return (E) extension;
        }
        return null;
    }

    public void dispose() {
        features.clear();
    }
}
