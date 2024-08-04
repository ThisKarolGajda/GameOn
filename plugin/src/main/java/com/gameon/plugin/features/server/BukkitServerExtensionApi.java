package com.gameon.plugin.features.server;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.server.IServerExtension;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitServerExtensionApi implements IServerExtension {
    private final Server server;
    private final Plugin plugin;

    public BukkitServerExtensionApi(Plugin plugin) {
        this.plugin = plugin;
        this.server = Bukkit.getServer();
    }

    @Override
    public LocalDateTime getUptime() {
        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
        LocalDateTime now = LocalDateTime.now();
        return now.minus(Duration.ofMillis(jvmUpTime));
    }
    @Override
    public String getGameOnVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public int getMaxPlayers() {
        return server.getMaxPlayers();
    }

    @Override
    public String getServerVersion() {
        return server.getVersion();
    }

    @Override
    public List<UserId> getBannedUsers() {
        return server.getBannedPlayers().stream()
                .map(player -> new UserId(player.getUniqueId(), player.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllowedDimensions() {
        List<String> allowedDimensions = new ArrayList<>();
        if (server.getAllowEnd()) {
            allowedDimensions.add("end");
        }
        if (server.getAllowNether()) {
            allowedDimensions.add("nether");
        }

        return allowedDimensions;
    }

    @Override
    public int getIdleTimeOut() {
        return server.getIdleTimeout();
    }

    @Override
    public List<String> getInitialEnabledPacks() {
        return server.getInitialDisabledPacks();
    }

    @Override
    public List<String> getInitialDisabledPacks() {
        return server.getInitialDisabledPacks();
    }

    @Override
    public String getMotd() {
        return server.getMotd();
    }

    @Override
    public String getDefaultGameMode() {
        return server.getDefaultGameMode().name();
    }

    @Override
    public int getSimulationDistance() {
        return server.getSimulationDistance();
    }

    @Override
    public List<String> getWorlds() {
        return server.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
    }

    @Override
    public int getViewDistance() {
        return server.getViewDistance();
    }

    @Override
    public List<UserId> getWhitelistedPlayers() {
        return server.getWhitelistedPlayers().stream()
                .map(player -> new UserId(player.getUniqueId(), player.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserId> getOnlinePlayers() {
        return server.getOnlinePlayers().stream()
                .map(player -> new UserId(player.getUniqueId(), player.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return server.getName();
    }

    @Override
    public String getAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException ignored) {
            return "Unknown";
        }
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}