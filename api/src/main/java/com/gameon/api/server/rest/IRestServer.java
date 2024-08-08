package com.gameon.api.server.rest;

import com.gameon.api.server.IGameOnApiServer;

public interface IRestServer {

    void initialize(RestServerSettings settings, IGameOnApiServer apiServer);

    void stop(RestServerStopReasonType stopReason);
}
