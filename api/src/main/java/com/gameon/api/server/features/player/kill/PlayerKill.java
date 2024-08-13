package com.gameon.api.server.features.player.kill;

import com.gameon.api.server.common.UserId;

import java.time.LocalDateTime;

public record PlayerKill(UserId killed, PlayerKillType killType, LocalDateTime date) {
}
