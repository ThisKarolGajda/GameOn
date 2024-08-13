package com.gameon.api.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommonProxy {
    private String channelName;
    private Map<String, Consumer<String>> messageHandlers;

    public CommonProxy(String channelName) {
        this.channelName = channelName;
        this.messageHandlers = new HashMap<>();
    }

    public void registerMessageHandler(String serverName, Consumer<String> handler) {
        messageHandlers.put(serverName, handler);
    }

    public void sendMessage(String serverName, String message) {
        Consumer<String> handler = messageHandlers.get(serverName);
        if (handler != null) {
            handler.accept(message);
        } else {
            System.out.println("No message handler registered for server: " + serverName);
        }
    }
}