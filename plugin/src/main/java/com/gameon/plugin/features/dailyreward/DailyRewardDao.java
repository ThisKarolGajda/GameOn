package com.gameon.plugin.features.dailyreward;

import java.time.LocalDateTime;
import java.util.UUID;

public class DailyRewardDao {
    private final UUID uuid;
    private final LocalDateTime claimedDate;

    public DailyRewardDao(UUID uuid, LocalDateTime claimedDate) {
        this.uuid = uuid;
        this.claimedDate = claimedDate;
    }

    public LocalDateTime getClaimedDate() {
        return claimedDate;
    }

    public UUID getId() {
        return uuid;
    }
}
