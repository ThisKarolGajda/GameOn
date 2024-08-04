package com.gameon.plugin;

import com.gameon.api.server.GameServerType;
import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.features.FeatureRegistrar;
import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.features.authentication.JwtAuthenticationExtension;
import com.gameon.api.server.rest.IRestServer;
import com.gameon.api.server.rest.JavalinRestServer;
import com.gameon.api.server.rest.RestServerSettings;
import com.gameon.api.server.rest.RestServerStopReasonType;
import com.gameon.plugin.command.GameOnCommand;
import com.gameon.plugin.features.economy.VaultEconomyExtensionApi;
import com.gameon.plugin.features.news.NewsExtensionApi;
import com.gameon.plugin.features.permission.BukkitPermissionExtensionApi;
import com.gameon.plugin.features.server.BukkitServerExtensionApi;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameOnPlugin extends JavaPlugin implements IGameOnApiServer {
    private JavalinRestServer restServer;
    private FeatureRegistrar featureRegistrar;

    @Override
    public void onEnable() {
        featureRegistrar = new FeatureRegistrar(this);
        featureRegistrar.registerExtension(GameOnFeatureType.ECONOMY, new VaultEconomyExtensionApi());
        featureRegistrar.registerExtension(GameOnFeatureType.AUTHENTICATION, new JwtAuthenticationExtension("TOTAL-SECRET-THAT-GOING-TO-MOVE-TO-CONFIG", 1000L * 60 * 60 * 24 * 30));
        featureRegistrar.registerExtension(GameOnFeatureType.SERVER, new BukkitServerExtensionApi(this));
        featureRegistrar.registerExtension(GameOnFeatureType.PERMISSION, new BukkitPermissionExtensionApi());
        featureRegistrar.registerExtension(GameOnFeatureType.NEWS, new NewsExtensionApi(this));

        restServer = (JavalinRestServer) new JavalinRestServer().init(new RestServerSettings(8080, featureRegistrar.getFeatures()), this);

        this.getCommand("gameon").setExecutor(new GameOnCommand(featureRegistrar.getExtension(GameOnFeatureType.AUTHENTICATION)));
    }

    @Override
    public void onDisable() {
        restServer.stop(RestServerStopReasonType.SERVER_STOPPED);
        restServer = null;
        featureRegistrar.dispose();
        featureRegistrar = null;
    }

    @Override
    public List<GameOnFeatureType> enabledFeatures() {
        return featureRegistrar.getEnabledFeatures();
    }

    @Override
    public GameServerType getServerType() {
        return GameServerType.MINECRAFT;
    }

    @Override
    public IRestServer getRestServer() {
        return restServer;
    }

    @Override
    public <E extends IExtension> @Nullable E getExtension(GameOnFeatureType gameOnFeatureType) {
        return featureRegistrar.getExtension(gameOnFeatureType);
    }
}
