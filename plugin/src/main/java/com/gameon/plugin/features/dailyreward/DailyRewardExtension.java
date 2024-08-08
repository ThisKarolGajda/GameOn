package com.gameon.plugin.features.dailyreward;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.dailyreward.IDailyRewardExtension;
import com.gameon.api.server.features.economy.IEconomyExtension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DailyRewardExtension implements IDailyRewardExtension {
    private final DailyRewardsDatabase database;

    public DailyRewardExtension(Plugin plugin) {
        database = new DailyRewardsDatabase();
        scheduleDailyReset(plugin);
    }

    private void scheduleDailyReset(Plugin plugin) {
        LocalTime now = LocalTime.now();

        long secondsUntilMidnight = (24 - now.getHour()) * 3600 - now.getMinute() * 60 - now.getSecond();
        long initialDelay = secondsUntilMidnight * 20L;

        new BukkitRunnable() {
            @Override
            public void run() {
                clearDatabase();
            }
        }.runTaskLater(plugin, initialDelay * 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                clearDatabase();
            }
        }.runTaskTimer(plugin, initialDelay * 20L, 24 * 60 * 60 * 20L);
    }

    private void clearDatabase() {
        database.clearAll();
    }

    @Override
    public boolean hasClaimed(UserId userId) {
        return database.getById(userId.uuid()).isPresent();
    }

    @Override
    public boolean claim(UserId userId) {
        System.out.println("HAS? " + hasClaimed(userId));
        if (hasClaimed(userId)) {
            return false;
        }

        String type = GameOnInstance.getRegistry().getSettingValue("DAILY_REWARD_TYPE");
        String value = GameOnInstance.getRegistry().getSettingValue("DAILY_REWARD_VALUE");
        System.out.println("TYPE: " + type + " VALUE: " + value);

        if (type == null || value == null) {
            return false;
        }

        double amount = Double.parseDouble(value);
        System.out.println("AMOUNT: " + amount);
        if (amount <= 0) {
            return false;
        }

        Player player = Bukkit.getPlayer(userId.uuid());
        System.out.println("PLAYER: " + player);
        if (player == null) {
            return false;
        }

        boolean success = false;
        if (type.equalsIgnoreCase("economy")) {
            IEconomyExtension extension = GameOnInstance.getFeatureRegistrar().getExtension("economy");
            if (extension == null) {
                return false;
            }

            extension.deposit(userId, amount);
            success = true;
        }

        if (success) {
            database.save(new DailyRewardDao(userId.uuid(), LocalDateTime.now()));
        }
        return success;
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
