package com.gameon.api.server.common;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID uuid, String username) {
    public static UserId fromUuid(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return new UserId(uuid, uuid.toString());
    }

    public static UserId fromUuidString(String uuid) {
        if (uuid == null) {
            return null;
        }

        return fromUuid(UUID.fromString(uuid));
    }

    public static UserId fromString(String userId) {
        if (userId == null) {
            return null;
        }

        int separatorIndex = userId.indexOf(':');
        if (separatorIndex == -1) {
            return new UserId(UUID.fromString(userId), userId);
        } else {
            String uuidString = userId.substring(0, separatorIndex);
            String username = userId.substring(separatorIndex + 1);
            return new UserId(UUID.fromString(uuidString), username);
        }
    }

    @Override
    public String toString() {
        return uuid.toString() + ":" + username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;
        return Objects.equals(uuid, userId.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username);
    }
}
