package com.gameon.api.server.features.server;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModuleInfo;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerInfo extends AbstractModuleInfo {
    private IServerExtension server;

    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.SERVER;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        Set<HandlerData> routes = new HashSet<>();
        server = (IServerExtension) extension;

        routes.add(new HandlerData("all-info", HandlerType.GET, HandlerAccessType.EVERYONE, this::getAllServerInfo));
        routes.add(new HandlerData("uptime", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getServerUptime));
        routes.add(new HandlerData("version", HandlerType.GET, HandlerAccessType.EVERYONE, this::getServerVersion));
        routes.add(new HandlerData("gameon-version", HandlerType.GET, HandlerAccessType.EVERYONE, this::getGameOnVersion));
        routes.add(new HandlerData("max-players", HandlerType.GET, HandlerAccessType.EVERYONE, this::getMaxPlayers));
        routes.add(new HandlerData("banned-users", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getBannedUsers));
        routes.add(new HandlerData("allowed-dimensions", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getAllowedDimensions));
        routes.add(new HandlerData("idle-timeout", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getIdleTimeout));
        routes.add(new HandlerData("enabled-packs", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getEnabledPacks));
        routes.add(new HandlerData("disabled-packs", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getDisabledPacks));
        routes.add(new HandlerData("motd", HandlerType.GET, HandlerAccessType.EVERYONE, this::getMotd));
        routes.add(new HandlerData("default-game-mode", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getDefaultGameMode));
        routes.add(new HandlerData("simulation-distance", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getSimulationDistance));
        routes.add(new HandlerData("worlds", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getWorlds));
        routes.add(new HandlerData("view-distance", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getViewDistance));
        routes.add(new HandlerData("whitelisted-players", HandlerType.GET, HandlerAccessType.AUTHORIZED, this::getWhitelistedPlayers));

        return routes;
    }

    private void getAllServerInfo(Context ctx) {
        Map<String, Object> entries = Map.ofEntries(
                Map.entry("version", server.getServerVersion()),
                Map.entry("gameonVersion", server.getGameOnVersion()),
                Map.entry("maxPlayers", server.getMaxPlayers()),
                Map.entry("onlinePlayers", server.getOnlinePlayers()),
                Map.entry("motd", server.getMotd()),
                Map.entry("uptime", server.getUptime()),
                Map.entry("bannedUsers", server.getBannedUsers()),
                Map.entry("allowedDimensions", server.getAllowedDimensions()),
                Map.entry("idleTimeout", server.getIdleTimeOut()),
                Map.entry("enabledPacks", server.getInitialEnabledPacks()),
                Map.entry("disabledPacks", server.getInitialDisabledPacks()),
                Map.entry("defaultGameMode", server.getDefaultGameMode()),
                Map.entry("simulationDistance", server.getSimulationDistance()),
                Map.entry("worlds", server.getWorlds()),
                Map.entry("viewDistance", server.getViewDistance()),
                Map.entry("whitelistedPlayers", server.getWhitelistedPlayers()),
                Map.entry("name", server.getName()),
                Map.entry("address", server.getAddress())
        );

        success(ctx, entries);
    }

    private void getServerUptime(Context ctx) {
        LocalDateTime uptime = server.getUptime();
        success(ctx, Map.of("uptime", uptime));
    }

    private void getServerVersion(Context ctx) {
        String version = server.getServerVersion();
        success(ctx, Map.of("version", version));
    }

    private void getGameOnVersion(Context ctx) {
        String version = server.getGameOnVersion();
        success(ctx, Map.of("gameonVersion", version));
    }

    private void getMaxPlayers(Context ctx) {
        int maxPlayers = server.getMaxPlayers();
        success(ctx, Map.of("maxPlayers", maxPlayers));
    }

    private void getBannedUsers(Context ctx) {
        List<UserId> bannedUsers = server.getBannedUsers();
        success(ctx, Map.of("bannedUsers", bannedUsers));
    }

    private void getAllowedDimensions(Context ctx) {
        List<String> allowedDimensions = server.getAllowedDimensions();
        success(ctx, Map.of("allowedDimensions", allowedDimensions));
    }

    private void getIdleTimeout(Context ctx) {
        int idleTimeout = server.getIdleTimeOut();
        success(ctx, Map.of("idleTimeout", idleTimeout));
    }

    private void getEnabledPacks(Context ctx) {
        List<String> enabledPacks = server.getInitialEnabledPacks();
        success(ctx, Map.of("enabledPacks", enabledPacks));
    }

    private void getDisabledPacks(Context ctx) {
        List<String> disabledPacks = server.getInitialDisabledPacks();
        success(ctx, Map.of("disabledPacks", disabledPacks));
    }

    private void getMotd(Context ctx) {
        String motd = server.getMotd();
        success(ctx, Map.of("motd", motd));
    }

    private void getDefaultGameMode(Context ctx) {
        String defaultGameMode = server.getDefaultGameMode();
        success(ctx, Map.of("defaultGameMode", defaultGameMode));
    }

    private void getSimulationDistance(Context ctx) {
        int simulationDistance = server.getSimulationDistance();
        success(ctx, Map.of("simulationDistance", simulationDistance));
    }

    private void getWorlds(Context ctx) {
        List<String> worlds = server.getWorlds();
        success(ctx, Map.of("worlds", worlds));
    }

    private void getViewDistance(Context ctx) {
        int viewDistance = server.getViewDistance();
        success(ctx, Map.of("viewDistance", viewDistance));
    }

    private void getWhitelistedPlayers(Context ctx) {
        List<UserId> whitelistedPlayers = server.getWhitelistedPlayers();
        success(ctx, Map.of("whitelistedPlayers", whitelistedPlayers));
    }

    @Override
    public String getDefaultPath() {
        return "server";
    }
}
