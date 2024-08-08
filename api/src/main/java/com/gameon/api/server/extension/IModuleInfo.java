package com.gameon.api.server.extension;

import com.gameon.api.server.extension.handler.HandlerData;

import java.util.Set;

public interface IModuleInfo {

    Set<HandlerData> getRoutes(IExtension extension);

    String getDefaultPath();
}
