package com.gameon.plugin.features.player.activity;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.IPlayerExtension;
import com.gameon.api.server.features.player.activity.PlayerActivity;
import com.gameon.api.server.features.player.activity.PlayerActivityType;
import com.gameon.plugin.features.player.PlayerBuilder;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ActivityMonitor extends BukkitRunnable {
    private final IPlayerExtension playerExtension;
    private final ActivityPointsManager activityPointsManager;
    private final Map<UserId, LocalDateTime> lastEndedActivities;

    public ActivityMonitor(IPlayerExtension playerExtension, ActivityPointsManager activityPointsManager) {
        this.playerExtension = playerExtension;
        this.activityPointsManager = activityPointsManager;
        this.lastEndedActivities = new HashMap<>();
    }

    @Override
    public void run() {
        for (UserId userId : activityPointsManager.getAllUserIds()) {
            PlayerActivityType currentActivityType = getCurrentActivity(userId);

            if (currentActivityType != null) {
                System.out.println("PlayerImpl " + userId + " is currently engaged in " + currentActivityType);
                saveActivity(userId, currentActivityType);
            }
        }
    }

    public void handlePlayerLeave(UserId userId) {
        PlayerActivityType currentActivityType = getCurrentActivity(userId);

        if (currentActivityType != null) {
            System.out.println("PlayerImpl " + userId + " left the server. Last engaged in " + currentActivityType +
                    " with " + activityPointsManager.getPoints(userId, currentActivityType) + " points.");
            saveActivity(userId, currentActivityType);
        }

        activityPointsManager.resetPoints(userId);
        lastEndedActivities.remove(userId);
    }

    @Nullable
    private PlayerActivityType getCurrentActivity(UserId userId) {
        PlayerActivityType currentActivityType = null;
        int maxPoints = 0;

        for (PlayerActivityType activityType : PlayerActivityType.values()) {
            int points = activityPointsManager.getPoints(userId, activityType);
            if (points > maxPoints) {
                maxPoints = points;
                currentActivityType = activityType;
            }
        }

        return currentActivityType != null && maxPoints >= 100 ? currentActivityType : null;
    }

    private void saveActivity(UserId userId, PlayerActivityType activityType) {
        LocalDateTime lastEndedActivity = lastEndedActivities.getOrDefault(userId, playerExtension.getPlayer(userId).lastJoinDate());
        PlayerActivity activity = new PlayerActivity(activityType, lastEndedActivity, LocalDateTime.now());

        PlayerBuilder.of(playerExtension.getPlayer(userId))
                .addPlayerActivity(activity)
                .save();

        lastEndedActivities.put(userId, LocalDateTime.now());
        activityPointsManager.resetPoints(userId);
    }
}
