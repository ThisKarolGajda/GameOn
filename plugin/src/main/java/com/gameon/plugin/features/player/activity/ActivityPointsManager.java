package com.gameon.plugin.features.player.activity;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.activity.PlayerActivityType;

import java.util.HashMap;
import java.util.Map;

public class ActivityPointsManager {
    private final Map<UserId, Map<PlayerActivityType, Integer>> activityPoints = new HashMap<>();

    public void addPoints(UserId userId, PlayerActivityType activityType, int points) {
        activityPoints.computeIfAbsent(userId, k -> new HashMap<>())
                .merge(activityType, points, Integer::sum);
        System.out.println("Added for: " + activityType + " " + points + " points to " + userId);
    }

    public int getPoints(UserId userId, PlayerActivityType activityType) {
        return activityPoints.getOrDefault(userId, new HashMap<>()).getOrDefault(activityType, 0);
    }

    public void resetPoints(UserId userId) {
        activityPoints.remove(userId);
    }

    public UserId[] getAllUserIds() {
        return activityPoints.keySet().toArray(new UserId[0]);
    }
}
