package com.gameon.api.server.extension;

import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.features.authentication.AuthenticationInfo;
import com.gameon.api.server.features.economy.EconomyInfo;
import org.jetbrains.annotations.Contract;

public class ModuleInfoFactory {
    @Contract(pure = true)
    public static IModuleInfo create(GameOnFeatureType feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }

        return switch (feature) {
            case AUTHENTICATION -> new AuthenticationInfo();
            case ECONOMY -> new EconomyInfo();
        };
    }
}