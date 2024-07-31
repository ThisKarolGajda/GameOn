package com.gameon.plugin;

import com.gameon.api.server.GameServerType;
import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.features.FeatureRegistrar;
import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.features.authentication.JwtAuthentication;
import com.gameon.api.server.rest.IRestServer;
import com.gameon.api.server.rest.JavalinRestServer;
import com.gameon.api.server.rest.RestServerSettings;
import com.gameon.api.server.rest.RestServerStopReasonType;
import com.gameon.plugin.command.GameOnCommand;
import com.gameon.plugin.features.economy.VaultEconomyApi;
import com.gameon.plugin.features.permission.BukkitPermissionApi;
import com.gameon.plugin.features.server.BukkitServerApi;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameOnPlugin extends JavaPlugin implements IGameOnApiServer {
    private JavalinRestServer restServer;
    private FeatureRegistrar featureRegistrar;

    @Override
    public void onEnable() {
        featureRegistrar = new FeatureRegistrar(this);
        featureRegistrar.registerExtension(GameOnFeatureType.ECONOMY, new VaultEconomyApi());
        featureRegistrar.registerExtension(GameOnFeatureType.AUTHENTICATION, new JwtAuthentication("TOTAL-SECRET-THAT-GOING-TO-MOVE-TO-CONFIG", 1000L * 60 * 60 * 24 * 30));
        featureRegistrar.registerExtension(GameOnFeatureType.SERVER, new BukkitServerApi());
        featureRegistrar.registerExtension(GameOnFeatureType.PERMISSION, new BukkitPermissionApi());

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
