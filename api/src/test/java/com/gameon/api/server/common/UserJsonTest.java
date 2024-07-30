package com.gameon.api.server.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserJsonTest {

    @Test
    void testSerializeUser() {
        UserId userId = new UserId(UUID.randomUUID(), "");
        User user = new User(userId, true, UserPrivilegeType.AUTHENTICATION);

        String json = UserJson.toJson(user);

        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    void testDeserializeUser() {
        UUID randomUUID = UUID.randomUUID();
        String json = "{\"id\":{\"uuid\":\"" + randomUUID + "\",\"username\":\"\"},\"logged\":true,\"privilege\":\"AUTHENTICATION\"}";

        User user = UserJson.fromJson(json);

        assertNotNull(user);
        assertEquals(randomUUID, user.getId().uuid());
        assertTrue(user.isLogged());
        assertEquals(UserPrivilegeType.AUTHENTICATION, user.getPrivilege());
    }

    @Test
    void testSerializeUserNull() {
        assertNull(UserJson.toJson(null));
    }

    @Test
    void testDeserializeInvalidJsonThrowsException() {
        String invalidJson = "{invalid json}";

        assertThrows(RuntimeException.class, () -> UserJson.fromJson(invalidJson));
    }
}
