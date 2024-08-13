package com.gameon.api.server.features.player.death;

import com.gameon.api.server.common.UserId;

import java.time.LocalDateTime;

public class PlayerDeathByPlayer extends PlayerDeath {
    private final UserId killer;

    public PlayerDeathByPlayer(LocalDateTime date, UserId killer) {
        super(PlayerDeathType.PLAYER, date);
        this.killer = killer;
    }

    public UserId getKiller() {
        return killer;
    }
}
