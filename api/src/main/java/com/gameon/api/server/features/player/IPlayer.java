package com.gameon.api.server.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.activity.PlayerActivity;
import com.gameon.api.server.features.player.death.PlayerDeath;
import com.gameon.api.server.features.player.kill.PlayerKill;
import com.gameon.api.server.features.player.reputation.PlayerReputation;

import java.time.LocalDateTime;
import java.util.List;

public interface IPlayer {

    UserId userId();

    List<PlayerDeath> playerDeaths();

    List<PlayerKill> playerKills();

    PlayerReputation playerReputation();

    List<PlayerActivity> playerActivities();

    LocalDateTime firstJoinDate();

    LocalDateTime lastJoinDate();
}
