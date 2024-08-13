package com.gameon.plugin.features.player;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.IPlayer;
import com.gameon.api.server.features.player.activity.PlayerActivity;
import com.gameon.api.server.features.player.death.PlayerDeath;
import com.gameon.api.server.features.player.death.PlayerDeathType;
import com.gameon.api.server.features.player.kill.PlayerKill;
import com.gameon.api.server.features.player.reputation.PlayerReputation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlayerBuilder {
    private UserId userId;
    private List<PlayerDeath> playerDeaths;
    private List<PlayerKill> playerKills;
    private PlayerReputation playerReputation;
    private List<PlayerActivity> playerActivities;
    private LocalDateTime firstJoinDate;
    private LocalDateTime lastJoinDate;

    public PlayerBuilder(@NotNull IPlayer player) {
        this.userId = player.userId();
        this.playerDeaths = new ArrayList<>(player.playerDeaths());
        this.playerKills = new ArrayList<>(player.playerKills());
        this.playerReputation = player.playerReputation();
        this.playerActivities = new ArrayList<>(player.playerActivities());
        this.firstJoinDate = player.firstJoinDate();
        this.lastJoinDate = player.lastJoinDate();
    }

    public PlayerBuilder(UserId userId) {
        this.userId = userId;
        this.playerDeaths = new ArrayList<>();
        this.playerKills = new ArrayList<>();
        this.playerReputation = new PlayerReputation();
        this.playerActivities = new ArrayList<>();
        this.firstJoinDate = LocalDateTime.now();
        this.lastJoinDate = LocalDateTime.now();
    }

    @NotNull
    @Contract("_ -> new")
    public static PlayerBuilder of(IPlayer player) {
        return new PlayerBuilder(player);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static PlayerBuilder of(UserId userId) {
        return new PlayerBuilder(userId);
    }

    public PlayerBuilder setUserId(UserId userId) {
        this.userId = userId;
        return this;
    }

    public PlayerBuilder setPlayerDeaths(List<PlayerDeath> playerDeaths) {
        this.playerDeaths = playerDeaths;
        return this;
    }

    public PlayerBuilder addPlayerDeath(PlayerDeathType type) {
        this.playerDeaths.add(new PlayerDeath(type, LocalDateTime.now()));
        return this;
    }

    public PlayerBuilder setPlayerKills(List<PlayerKill> playerKills) {
        this.playerKills = playerKills;
        return this;
    }

    public PlayerBuilder addPlayerKill(PlayerKill playerKill) {
        this.playerKills.add(playerKill);
        return this;
    }

    public PlayerBuilder setPlayerReputation(PlayerReputation playerReputation) {
        this.playerReputation = playerReputation;
        return this;
    }

    public PlayerBuilder setPlayerActivities(List<PlayerActivity> playerActivities) {
        this.playerActivities = playerActivities;
        return this;
    }

    public PlayerBuilder addPlayerActivity(PlayerActivity playerActivity) {
        this.playerActivities.add(playerActivity);
        return this;
    }

    public PlayerBuilder setFirstJoinDate(LocalDateTime firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
        return this;
    }

    public PlayerBuilder setLastJoinDate(LocalDateTime lastJoinDate) {
        this.lastJoinDate = lastJoinDate;
        return this;
    }

    public PlayerImpl build() {
        return new PlayerImpl(
                userId,
                playerDeaths != null ? playerDeaths : new ArrayList<>(),
                playerKills != null ? playerKills : new ArrayList<>(),
                playerReputation != null ? playerReputation : new PlayerReputation(),
                playerActivities != null ? playerActivities : new ArrayList<>(),
                firstJoinDate != null ? firstJoinDate : LocalDateTime.now(),
                lastJoinDate != null ? lastJoinDate : LocalDateTime.now()
        );
    }

    public void save() {
        PlayerExtension extension = GameOnInstance.getFeatureRegistrar().getExtension("PLAYER");
        if (extension != null && extension.canBeUsed()) {
            extension.savePlayer(build());
        }
    }

    public PlayerBuilder setLastJoinDate() {
        return setLastJoinDate(LocalDateTime.now());
    }
}
