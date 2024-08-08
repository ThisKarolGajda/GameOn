package com.gameon.plugin.features.dailyreward;

import com.gameon.api.server.database.JSONDatabase;

import java.util.UUID;

public class DailyRewardsDatabase extends JSONDatabase<UUID, DailyRewardDao> {
    public DailyRewardsDatabase() {
        super("daily-rewards", DailyRewardDao[].class, false);
    }

    public void clearAll() {
        for (DailyRewardDao dailyReward : getAll()) {
            delete(dailyReward.getId());
        }
    }
}
