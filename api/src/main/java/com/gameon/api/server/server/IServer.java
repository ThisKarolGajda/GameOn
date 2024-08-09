package com.gameon.api.server.server;

import com.gameon.api.server.IGameOnApiServer;

public interface IServer {

    void initialize(ServerSettings settings, IGameOnApiServer apiServer);

    void stop(ServerStopReasonType stopReason);
}
