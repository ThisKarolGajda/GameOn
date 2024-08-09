package com.gameon.api.server.features.colorpanel;

import com.gameon.api.server.GameOnInstance;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.EndpointHandlerData;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.Map;
import java.util.Set;

public class ColorPanelModule extends AbstractModule {
    private static final Map<String, String> COLOR_SETTINGS = Map.of(
            "accent", "COLOR_PANEL_ACCENT",
            "secondary", "COLOR_PANEL_SECONDARY",
            "text", "COLOR_PANEL_TEXT",
            "background", "COLOR_PANEL_BACKGROUND"
    );

    @Override
    public Set<EndpointHandlerData> getEndpoints(IExtension extension) {
        return Set.of(new EndpointHandlerData(
                "colors",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                this::getColors
        ));
    }

    private void getColors(Context ctx) {
        Map<String, String> colors = COLOR_SETTINGS.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> GameOnInstance.getRegistry().getSettingValue(entry.getValue())
                ));

        success(ctx, colors);
    }

    @Override
    public String getDefaultPath() {
        return "color-panel";
    }
}