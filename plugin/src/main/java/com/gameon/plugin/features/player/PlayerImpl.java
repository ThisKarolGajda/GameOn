package com.gameon.plugin.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.IPlayer;
import com.gameon.api.server.features.player.activity.PlayerActivity;
import com.gameon.api.server.features.player.death.PlayerDeath;
import com.gameon.api.server.features.player.kill.PlayerKill;
import com.gameon.api.server.features.player.reputation.PlayerReputation;

import java.time.LocalDateTime;
import java.util.List;

public record PlayerImpl(UserId userId, List<PlayerDeath> playerDeaths, List<PlayerKill> playerKills,
                         PlayerReputation playerReputation,
                         List<PlayerActivity> playerActivities, LocalDateTime firstJoinDate,
                         LocalDateTime lastJoinDate) implements IPlayer {

    public PlayerImpl copyWith(
            UserId userId,
            List<PlayerDeath> playerDeaths,
            List<PlayerKill> playerKills,
            PlayerReputation playerReputation,
            List<PlayerActivity> playerActivities,
            LocalDateTime firstJoinDate,
            LocalDateTime lastJoinDate) {
        return new PlayerImpl(
                userId != null ? userId : this.userId,
                playerDeaths != null ? playerDeaths : this.playerDeaths,
                playerKills != null ? playerKills : this.playerKills,
                playerReputation != null ? playerReputation : this.playerReputation,
                playerActivities != null ? playerActivities : this.playerActivities,
                firstJoinDate != null ? firstJoinDate : this.firstJoinDate,
                lastJoinDate != null ? lastJoinDate : this.lastJoinDate
        );
    }
}
