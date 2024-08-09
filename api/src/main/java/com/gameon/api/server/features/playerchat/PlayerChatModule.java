package com.gameon.api.server.features.playerchat;

import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.WebSocketHandlerData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerChatModule extends AbstractModule {

    @Override
    public Set<WebSocketHandlerData> getWebSockets(IExtension extension) {
        IPlayerChatExtension chatExtension = (IPlayerChatExtension) extension;

        chatExtension.getSubscription().onCalled((userId, message) -> broadcastWebSocketMessage(Map.of(
                "uuid", userId.uuid().toString(),
                "message", message,
                "source", "Minecraft"
        )));

        Set<WebSocketHandlerData> routes = new HashSet<>();

        routes.add(new WebSocketHandlerData(
                "chat",
                HandlerAccessType.AUTHORIZED,
                (ctx, userId) -> {
                    Map<String, Object> json = deserialize(ctx);
                    if (json == null) {
                        return;
                    }

                    String message = (String) json.get("message");
                    String source = (String) json.get("source");
                    if (message == null || source == null) {
                       return;
                    }

                    chatExtension.sendMessage(userId, message, source);
                }
        ));

        return routes;
    }

    @Override
    public String getDefaultPath() {
        return "player-chat";
    }
}
