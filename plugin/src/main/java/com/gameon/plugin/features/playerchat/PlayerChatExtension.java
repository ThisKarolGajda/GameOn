package com.gameon.plugin.features.playerchat;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.playerchat.IPlayerChatExtension;
import com.gameon.api.server.features.playerchat.PlayerChatSubscription;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatExtension implements IPlayerChatExtension, Listener {
    private final PlayerChatSubscription playerChatSubscription = new PlayerChatSubscription();
    @Override
    public void sendMessage(UserId userId, String message, String source) {
        Player player = Bukkit.getPlayer(userId.uuid());
        //todo: add formating
        String format = source + " " + (player != null ? player.getDisplayName() : userId.username()) + ": %s";
        String formattedMessage = String.format(format, message);
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.sendMessage(formattedMessage));
    }

    @Override
    public PlayerChatSubscription getSubscription() {
        return playerChatSubscription;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        playerChatSubscription.call(new UserId(event.getPlayer().getUniqueId(), event.getPlayer().getName()), event.getMessage());
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
