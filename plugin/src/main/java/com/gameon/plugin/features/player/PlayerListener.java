package com.gameon.plugin.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.IPlayer;
import com.gameon.api.server.features.player.death.PlayerDeathType;
import com.gameon.plugin.GameOnPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final GameOnPlugin plugin;
    private final PlayerExtension playerExtension;

    public PlayerListener(GameOnPlugin plugin, PlayerExtension playerExtension) {
        this.plugin = plugin;
        this.playerExtension = playerExtension;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
        IPlayer iPlayer = playerExtension.getPlayer(userId);

        PlayerBuilder builder = PlayerBuilder.of(iPlayer);
        builder.setLastJoinDate().save();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UserId userId = UserId.fromUuid(event.getEntity().getUniqueId());
        IPlayer iPlayer = playerExtension.getPlayer(userId);

        if (iPlayer instanceof PlayerImpl playerImpl) {
            PlayerBuilder.of(playerImpl)
                    // TODO: add type selection logic
                    .addPlayerDeath(PlayerDeathType.PLAYER)
                    .save();
        }
    }
}
