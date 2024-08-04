package com.gameon.api.server.features.adminsettings;

import java.util.HashMap;
import java.util.Map;

public class AdminSettingsRegistry {
    private final Map<String, Setting> settings = new HashMap<>();

    public AdminSettingsRegistry() {
        // DAILY REWARD
        settings.put("DAILY_REWARD_ENABLED", new Setting(Boolean.class, true)); // false, true
        settings.put("DAILY_REWARD_TYPE", new Setting(String.class, "economy")); // economy
        settings.put("DAILY_REWARD_VALUE", new Setting(String.class, "0"));
    }

    public <T> T getSetting(String key) {
        Setting setting = settings.get(key);
        if (setting != null) {
            return (T) setting.value;
        }
        return null;
    }

    public void updateSetting(String key, Object value) {
        Setting setting = settings.get(key);
        if (setting != null && value.getClass() == setting.expectedObjectClass) {
            setting.value = value;
        } else {
            throw new IllegalArgumentException("Invalid type for setting: " + key);
        }
    }

    private static class Setting {
        private final Class<?> expectedObjectClass;
        private Object value;

        public Setting(Class<?> expectedObjectClass, Object value) {
            this.expectedObjectClass = expectedObjectClass;
            this.value = value;
        }
    }
}
