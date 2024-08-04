package com.gameon.plugin.features.permission;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.permission.IPermissionExtension;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

public class BukkitPermissionExtensionApi implements IPermissionExtension {
    @Override
    public boolean isAdmin(UserId userId) {
        System.out.println("Is admin? " + userId);
        OfflinePlayer player = Bukkit.getOfflinePlayer(userId.uuid());
        if (!Objects.equals(player.getName(), userId.username())) {
            return false;
        }

        return player.isOp();
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
