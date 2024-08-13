package com.gameon.plugin.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.IPlayer;
import com.gameon.api.server.features.player.IPlayerExtension;
import com.gameon.plugin.features.player.activity.ActivityListener;
import com.gameon.plugin.features.player.activity.ActivityMonitor;
import com.gameon.plugin.features.player.activity.ActivityPointsManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;

public class PlayerExtension implements IPlayerExtension {
    private static final String PLAYER_DATA_DIR = "players" + File.separator;
    private static final int CACHE_EXPIRATION_MINUTES = 3;
    private static final int CACHE_MAX_SIZE = 250;

    private final Cache<UserId, IPlayer> playerCache;
    private final PlayerDataManager playerDataManager;

    public PlayerExtension(@NotNull Plugin plugin) {
        File dataDir = new File(plugin.getDataFolder(), PLAYER_DATA_DIR);
        playerCache = CacheBuilder.newBuilder()
                .expireAfterAccess(CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAX_SIZE)
                .build();
        playerDataManager = new PlayerDataManager(dataDir);

        ActivityPointsManager activityPointsManager = new ActivityPointsManager();
        ActivityMonitor activityMonitor = new ActivityMonitor(this, activityPointsManager);
        activityMonitor.runTaskTimerAsynchronously(plugin, 20 * 5, 20 * 5);
        getServer().getPluginManager().registerEvents(new ActivityListener(plugin, activityPointsManager), plugin);
    }

    @Override
    public IPlayer getPlayer(UserId userId) {
        try {
            return playerCache.get(userId, () -> playerDataManager.loadPlayerData(userId));
        } catch (Exception e) {
            return PlayerBuilder.of(userId).build();
        }
    }


    @Override
    public void savePlayer(IPlayer player) {
        playerDataManager.savePlayerData((PlayerImpl) player);
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
