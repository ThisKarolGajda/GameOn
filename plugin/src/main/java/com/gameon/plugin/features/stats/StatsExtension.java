package com.gameon.plugin.features.stats;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.features.economy.IEconomyExtension;
import com.gameon.api.server.features.stats.IStatsExtension;
import com.gameon.api.server.features.stats.StatType;
import com.gameon.plugin.GameOnPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StatsExtension implements IStatsExtension {
    private final GameOnPlugin plugin;
    private final Path filePath;
    private final ConcurrentHashMap<UUID, long[]> playerStatsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, long[]> pendingUpdates = new ConcurrentHashMap<>();
    private BukkitTask autoSaveTask;

    public StatsExtension(@NotNull GameOnPlugin plugin) {
        this.plugin = plugin;
        this.filePath = plugin.getDataFolder().toPath().resolve("player_stats.dat");
        loadStats();
        startAutoSave();
    }

    private void startAutoSave() {
        long saveIntervalSeconds = 60;
        autoSaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveStats();
            }
        }.runTaskTimerAsynchronously(plugin, saveIntervalSeconds * 20, saveIntervalSeconds * 20);
    }

    private void loadStats() {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(filePath.toFile()));
                 DataInputStream dis = new DataInputStream(gis)) {
                while (dis.available() > 0) {
                    String userIdString = dis.readUTF();
                    UserId userId = UserId.fromString(userIdString);
                    long[] stats = new long[StatType.values().length];
                    for (StatType statType : StatType.values()) {
                        stats[statType.getIndex()] = dis.readLong();
                    }

                    playerStatsMap.put(userId.uuid(), stats);
                }
            } catch (IOException e) {
                System.out.println("Could not load player stats: " + e);
            }
        } catch (IOException e) {
            System.out.println("Could not load player stats file: " + e);
        }
    }

    @Override
    public long getValueSummed(StatType statType) {
        long total = 0;
        for (long[] stats : playerStatsMap.values()) {
            total += stats[statType.getIndex()];
        }
        return total;
    }

    @Override
    public long getValue(StatType statType, UserId userId) {
        long[] stats = playerStatsMap.get(userId.uuid());
        return stats != null ? stats[statType.getIndex()] : 0;
    }

    public void updatePlayerStats(UserId userId, StatType statType, long value) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long[] stats = pendingUpdates.getOrDefault(userId.uuid(), playerStatsMap.getOrDefault(userId.uuid(), new long[StatType.values().length]));
            stats[statType.getIndex()] += value;

            IExtension extension = plugin.getFeatureRegistrar().getExtension("ECONOMY");
            if (extension != null) {
                IEconomyExtension economyExtension = (IEconomyExtension) extension;
                stats[StatType.MONEY.getIndex()] = (long) economyExtension.getBalance(userId);
            }

            pendingUpdates.put(userId.uuid(), stats);
        });
    }

    private void saveStats() {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filePath.toFile()))))) {
            for (var entry : pendingUpdates.entrySet()) {
                UUID uuid = entry.getKey();
                long[] stats = entry.getValue();
                playerStatsMap.put(uuid, stats);
            }

            pendingUpdates.clear();
            for (var entry : playerStatsMap.entrySet()) {
                UUID uuid = entry.getKey();
                long[] stats = entry.getValue();
                dos.writeUTF(uuid.toString());
                for (StatType statType : StatType.values()) {
                    dos.writeLong(stats[statType.getIndex()]);
                }
            }

            dos.flush();
        } catch (IOException e) {
            System.out.println("Could not save player stats: " + e.getMessage());
        }
    }


    @Override
    public boolean canBeUsed() {
        return true;
    }

    public void cancelAutoSave() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
    }
}
