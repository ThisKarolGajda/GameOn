package com.gameon.api.server.features.playerchat;

import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.WebSocketHandlerData;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerChatModule extends AbstractModule {

    @Override
    public Set<WebSocketHandlerData> getWebSockets(IExtension extension) {
        IPlayerChatExtension chatExtension = (IPlayerChatExtension) extension;

        chatExtension.getSubscription().onCalled((userId, message) -> broadcastWebSocketMessage(Map.of(
                "uuid", userId.uuid().toString(),
                "nickname", userId.username(),
                "message", message,
                "source", "Minecraft",
                "date", LocalDateTime.now()
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
                    broadcastWebSocketMessage(Map.of(
                            "uuid", userId.uuid().toString(),
                            "nickname", userId.username(),
                            "message", message,
                            "source", source,
                            "date", LocalDateTime.now()
                    ));
                }
        ));

        return routes;
    }

    @Override
    public String getDefaultPath() {
        return "player-chat";
    }
}
