package com.gameon.api.server.features.player.activity;

import java.time.LocalDateTime;

public record PlayerActivity(PlayerActivityType type, LocalDateTime start, LocalDateTime end) {

}
