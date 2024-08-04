package com.gameon.api.server.features.adminsettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminSettingsRegistryTest {

    private AdminSettingsRegistry settingsRegistry;

    @BeforeEach
    void setUp() {
        settingsRegistry = new AdminSettingsRegistry();
    }

    @Test
    void testGetSetting() {
        // Test retrieving existing settings
        assertTrue((boolean) settingsRegistry.getSetting("DAILY_REWARD_ENABLED"));
        assertEquals("economy", settingsRegistry.getSetting("DAILY_REWARD_TYPE"));
        assertEquals("0", settingsRegistry.getSetting("DAILY_REWARD_VALUE"));
    }

    @Test
    void testUpdateSetting() {
        settingsRegistry.updateSetting("DAILY_REWARD_ENABLED", false);
        assertFalse((boolean) settingsRegistry.getSetting("DAILY_REWARD_ENABLED"));

        settingsRegistry.updateSetting("DAILY_REWARD_TYPE", "premium");
        assertEquals("premium", settingsRegistry.getSetting("DAILY_REWARD_TYPE"));

        settingsRegistry.updateSetting("DAILY_REWARD_VALUE", "100");
        assertEquals("100", settingsRegistry.getSetting("DAILY_REWARD_VALUE"));
    }

    @Test
    void testUpdateSettingWithInvalidType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> settingsRegistry.updateSetting("DAILY_REWARD_ENABLED", "not_a_boolean"));

        assertEquals("Invalid type for setting: DAILY_REWARD_ENABLED", exception.getMessage());
    }

    @Test
    void testGetNonExistentSetting() {
        assertNull(settingsRegistry.getSetting("NON_EXISTENT_SETTING"));
    }
}
