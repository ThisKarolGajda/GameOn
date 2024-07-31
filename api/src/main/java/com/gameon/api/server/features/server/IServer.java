package com.gameon.api.server.features.server;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

import java.time.LocalDateTime;
import java.util.List;

public interface IServer extends IExtension {

    LocalDateTime getUptime();

    String getGameOnVersion();

    int getMaxPlayers();

    String getServerVersion();

    List<UserId> getBannedUsers();

    List<String> getAllowedDimensions();

    int getIdleTimeOut();

    List<String> getInitialEnabledPacks();

    List<String> getInitialDisabledPacks();

    String getMotd();

    String getDefaultGameMode();

    int getSimulationDistance();

    List<String> getWorlds();

    int getViewDistance();

    List<UserId> getWhitelistedPlayers();

    List<UserId> getOnlinePlayers();
}
