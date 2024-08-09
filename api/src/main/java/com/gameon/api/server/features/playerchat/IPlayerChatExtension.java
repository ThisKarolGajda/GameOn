package com.gameon.api.server.features.playerchat;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IPlayerChatExtension extends IExtension {

    void sendMessage(UserId userId, String message, String source);

    PlayerChatSubscription getSubscription();
}

