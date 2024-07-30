package com.gameon.api.server.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void testFromStringWithUuidAndUsername() {
        String userId = "123e4567-e89b-12d3-a456-426614174000:testuser";
        UserId userIdObject = UserId.fromString(userId);

        assertNotNull(userIdObject);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), userIdObject.uuid());
        assertEquals("testuser", userIdObject.username());
    }

    @Test
    void testFromStringWithUuidOnly() {
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        UserId userIdObject = UserId.fromString(userId);

        assertNotNull(userIdObject);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), userIdObject.uuid());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", userIdObject.username());
    }

    @Test
    void testFromStringWithNull() {
        UserId userIdObject = UserId.fromString(null);
        assertNull(userIdObject);
    }

    @Test
    void testToString() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String username = "testuser";
        UserId userIdObject = new UserId(uuid, username);

        String userId = userIdObject.toString();
        assertEquals("123e4567-e89b-12d3-a456-426614174000:testuser", userId);
    }
}
