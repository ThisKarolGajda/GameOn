package com.gameon.api.server;

import com.gameon.api.server.adminsettings.AdminSettingsModule;
import com.gameon.api.server.adminsettings.AdminSettingsRegistry;
import com.gameon.api.server.features.FeatureRegistry;
import com.gameon.api.server.features.colorpanel.ColorPanelModule;
import com.gameon.api.server.rest.IRestServer;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.logging.Logger;

public class GameOnInstance {
    private static IGameOnApiServer instance;
    private static AdminSettingsRegistry registry;

    private GameOnInstance() {}

    public static void initialize(IGameOnApiServer apiServer) {
        instance = apiServer;
        registry = new AdminSettingsRegistry();
        apiServer.getFeatureRegistrar().registerExtension("ADMIN_SETTINGS", null, new AdminSettingsModule());
        if (registry.getSettingValue("COLOR_PANEL_ENABLED")) {
            apiServer.getFeatureRegistrar().registerExtension("COLOR_PANEL", null, new ColorPanelModule());
        }
    }

    @Contract(pure = true)
    public static IGameOnApiServer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("IGameOnApiServer instance is not initialized");
        }
        return instance;
    }

    public static GameServerType getServerType() {
        return getInstance().getServerType();
    }

    public static Logger getLogger() {
        return getInstance().getLogger();
    }

    public static IRestServer getRestServer() {
        return getInstance().getRestServer();
    }

    public static FeatureRegistry getFeatureRegistrar() {
        return getInstance().getFeatureRegistrar();
    }

    public static boolean isFeatureEnabled(String feature) {
        return getInstance().isFeatureEnabled(feature);
    }

    public static File getDataFolder() {
        return getInstance().getDataFolder();
    }

    public static AdminSettingsRegistry getRegistry() {
        return registry;
    }
}
