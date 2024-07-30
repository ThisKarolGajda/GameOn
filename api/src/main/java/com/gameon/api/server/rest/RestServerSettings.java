package com.gameon.api.server.rest;

import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.features.GameOnFeatureType;

import java.util.Map;

public record RestServerSettings(int port, Map<GameOnFeatureType, IExtension> features) {


}
