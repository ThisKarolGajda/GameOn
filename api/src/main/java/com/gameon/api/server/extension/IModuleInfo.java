package com.gameon.api.server.extension;

import com.gameon.api.server.extension.handler.EndpointHandlerData;
import com.gameon.api.server.extension.handler.WebSocketHandlerData;

import java.util.Collections;
import java.util.Set;

public interface IModuleInfo {

    default Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        return Collections.emptySet();
    }

    String getDefaultPath();

    default Set<WebSocketHandlerData> getWebSockets(IExtension extension) {
        return Collections.emptySet();
    }
}
