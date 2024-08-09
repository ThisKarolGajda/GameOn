package com.gameon.api.server.features.playerchat;

import com.gameon.api.server.common.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PlayerChatSubscription {
    private final List<BiConsumer<UserId, String>> listeners = new ArrayList<>();

    public void call(UserId userId, String message) {
        for (BiConsumer<UserId, String> listener : listeners) {
            listener.accept(userId, message);
        }
    }

    public void onCalled(BiConsumer<UserId, String> listener) {
        listeners.add(listener);
    }
}
