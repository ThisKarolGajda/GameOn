package com.gameon.plugin;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.GameServerType;
import com.gameon.api.server.IGameOnApiServer;
import com.gameon.api.server.features.FeatureRegistry;
import com.gameon.api.server.features.authentication.AuthenticationModule;
import com.gameon.api.server.features.authentication.JwtAuthenticationExtension;
import com.gameon.api.server.features.dailyreward.DailyRewardModule;
import com.gameon.api.server.features.economy.EconomyModule;
import com.gameon.api.server.features.news.NewsExtension;
import com.gameon.api.server.features.news.NewsModule;
import com.gameon.api.server.features.permission.PermissionModule;
import com.gameon.api.server.features.player.PlayerModule;
import com.gameon.api.server.features.playerchat.PlayerChatModule;
import com.gameon.api.server.features.server.ServerModule;
import com.gameon.api.server.features.stats.StatsModule;
import com.gameon.api.server.server.IServer;
import com.gameon.api.server.server.JavalinServer;
import com.gameon.api.server.server.ServerSettings;
import com.gameon.api.server.server.ServerStopReasonType;
import com.gameon.plugin.command.GameOnCommand;
import com.gameon.plugin.features.dailyreward.DailyRewardExtension;
import com.gameon.plugin.features.economy.VaultEconomyExtension;
import com.gameon.plugin.features.permission.BukkitPermissionExtension;
import com.gameon.plugin.features.player.PlayerExtension;
import com.gameon.plugin.features.player.PlayerListener;
import com.gameon.plugin.features.playerchat.PlayerChatExtension;
import com.gameon.plugin.features.server.BukkitServerExtension;
import com.gameon.plugin.features.stats.StatsExtension;
import com.gameon.plugin.features.stats.StatsListener;
import org.bukkit.plugin.java.JavaPlugin;

public class GameOnPlugin extends JavaPlugin implements IGameOnApiServer {
    private JavalinServer server;
    private FeatureRegistry featureRegistry;
    private StatsExtension statsExtension;
    private PlayerExtension playerExtension;

    @Override
    public void onEnable() {
        featureRegistry = new FeatureRegistry(this);

        GameOnInstance.initialize(this);

        registerFeatures();
        registerRestServer();
        registerCommands();
    }

    private void registerRestServer() {
        server = new JavalinServer();
        server.initialize(new ServerSettings(8080), this);
    }

    private void registerFeatures() {
        getFeatureRegistrar().registerExtension("ECONOMY", new VaultEconomyExtension(), new EconomyModule());
        getFeatureRegistrar().registerExtension("AUTHENTICATION", new JwtAuthenticationExtension("TOTAL-SECRET-THAT-GOING-TO-MOVE-TO-CONFIG", 1000L * 60 * 60 * 24 * 90), new AuthenticationModule());
        getFeatureRegistrar().registerExtension("SERVER", new BukkitServerExtension(this), new ServerModule());
        getFeatureRegistrar().registerExtension("PERMISSION", new BukkitPermissionExtension(), new PermissionModule());
        getFeatureRegistrar().registerExtension("NEWS", new NewsExtension(), new NewsModule());

        if (GameOnInstance.getRegistry().getSettingValue("DAILY_REWARD_ENABLED")) {
            getFeatureRegistrar().registerExtension("DAILY_REWARD", new DailyRewardExtension(this), new DailyRewardModule());
        }

        PlayerChatExtension playerChatExtension = new PlayerChatExtension();
        featureRegistry.registerExtension("PLAYER_CHAT", playerChatExtension, new PlayerChatModule());
        getServer().getPluginManager().registerEvents(playerChatExtension, this);

        statsExtension = new StatsExtension(this);
        featureRegistry.registerExtension("STATS", statsExtension, new StatsModule());
        getServer().getPluginManager().registerEvents(new StatsListener(this, statsExtension), this);

        playerExtension = new PlayerExtension(this);
        featureRegistry.registerExtension("PLAYER", playerExtension, new PlayerModule());
        getServer().getPluginManager().registerEvents(new PlayerListener(this, playerExtension), this);
    }

    private void registerCommands() {
        this.getCommand("gameon").setExecutor(new GameOnCommand(featureRegistry.getExtension("AUTHENTICATION")));
    }

    @Override
    public void onDisable() {
        if (statsExtension != null) {
            System.out.println("Cancelling auto save stats!");
            statsExtension.cancelAutoSave();
        }
        featureRegistry.dispose();
        featureRegistry = null;
        server.stop(ServerStopReasonType.SERVER_STOPPED);
        server = null;
    }

    @Override
    public GameServerType getServerType() {
        return GameServerType.MINECRAFT;
    }

    @Override
    public IServer getGameOnServer() {
        return server;
    }

    @Override
    public FeatureRegistry getFeatureRegistrar() {
        return featureRegistry;
    }
}
