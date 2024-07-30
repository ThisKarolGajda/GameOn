package com.gameon.api.server.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testCreateUser() {
        UserId userId = new UserId(UUID.randomUUID(), "testuser");
        boolean logged = true;
        UserPrivilegeType privilege = UserPrivilegeType.AUTHENTICATION;

        User user = new User(userId, logged, privilege);

        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertTrue(user.isLogged());
        assertEquals(privilege, user.getPrivilege());
    }

    @Test
    void testSetUserProperties() {
        UserId userId = new UserId(UUID.randomUUID(), "testuser");
        boolean logged = true;
        UserPrivilegeType privilege = UserPrivilegeType.AUTHENTICATION;
        User user = new User(userId, logged, privilege);

        UserId newUserId = new UserId(UUID.randomUUID(), "newuser");
        boolean newLogged = false;
        UserPrivilegeType newPrivilege = UserPrivilegeType.ANONYMOUS;
        user.setId(newUserId);
        user.setLogged(newLogged);
        user.setPrivilege(newPrivilege);

        assertEquals(newUserId, user.getId());
        assertFalse(user.isLogged());
        assertEquals(newPrivilege, user.getPrivilege());
    }
}
