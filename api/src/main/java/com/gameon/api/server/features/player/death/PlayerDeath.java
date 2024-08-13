package com.gameon.api.server.features.player.death;

import java.time.LocalDateTime;

public class PlayerDeath {
    private final PlayerDeathType type;
    private final LocalDateTime date;

    public PlayerDeath(PlayerDeathType type, LocalDateTime date) {
        this.type = type;
        this.date = date;
    }

    public PlayerDeathType getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
