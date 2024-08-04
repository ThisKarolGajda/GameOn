package com.gameon.plugin.command;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.authentication.ITokenAuthenticationExtension;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameOnCommand implements CommandExecutor {
    private final ITokenAuthenticationExtension authentication;

    public GameOnCommand(ITokenAuthenticationExtension authentication) {
        this.authentication = authentication;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        String token = authentication.generatePairingToken(new UserId(player.getUniqueId(), player.getName()));

        TextComponent tokenMessage = new TextComponent("Your token: ");
        TextComponent tokenComponent = new TextComponent(token);
        TextComponent tokenMessage2 = new TextComponent(". Enter it in the GameOn mobile app!");
        tokenComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy").create()));
        tokenComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, token));
        player.spigot().sendMessage(tokenMessage, tokenComponent, tokenMessage2);
        return true;
    }
}
