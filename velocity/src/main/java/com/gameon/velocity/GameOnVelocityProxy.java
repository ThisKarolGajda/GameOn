package com.gameon.velocity;

import com.gameon.api.proxy.CommonProxy;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "velocity",
        name = "GameOnVelocityProxy",
        version = "1.0-SNAPSHOT"
)
public class GameOnVelocityProxy {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private CommonProxy commonProxy;

    @Inject
    public GameOnVelocityProxy(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.commonProxy = new CommonProxy("YourChannelName");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        commonProxy.registerMessageHandler("server1", message -> {
            logger.info("Received message for server1: {}", message);
        });

        commonProxy.registerMessageHandler("server2", message -> {
            logger.info("Received message for server2: {}", message);
        });
    }

    public void sendMessage(String serverName, String message) {
        commonProxy.sendMessage(serverName, message);
    }
}