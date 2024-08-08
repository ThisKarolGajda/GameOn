package com.gameon.api.server.adminsettings;

import com.gameon.api.server.GameOnInstance;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class AdminSettingsRegistry {
    private final Set<AdminRegistryValue> settings = new LinkedHashSet<>();
    private final AdminSettingsDatabase database;

    public AdminSettingsRegistry() {
        this.database = new AdminSettingsDatabase();
        loadSettingsFromDatabase();
        initializeDefaultSettings();
    }

    private void loadSettingsFromDatabase() {
        for (AdminRegistryValue registryValue : database.getAll()) {
            if (registryValue.value != null) {
                settings.add(registryValue);
            }
        }
    }

    private void initializeDefaultSettings() {
        // REST API
        addDefaultSetting("REST_DISPLAY_ROUTES", boolean.class, true);
        addDefaultSetting("REST_DISPLAY_FEATURES", boolean.class, true);

        // HTTPS
        addDefaultSetting("HTTPS_ENABLED", boolean.class, false);
        addDefaultSetting("HTTPS_TOKEN", String.class, "");

        // DAILY REWARD
        addDefaultSetting("DAILY_REWARD_ENABLED", boolean.class, true);
        addDefaultSetting("DAILY_REWARD_TYPE", String.class, "economy"); // economy
        addDefaultSetting("DAILY_REWARD_VALUE", String.class, "0");

        // COLOR PANEL
        addDefaultSetting("COLOR_PANEL_ENABLED", boolean.class, false);
        addDefaultSetting("COLOR_PANEL_ACCENT", String.class, "0xFF37B0D9");
        addDefaultSetting("COLOR_PANEL_SECONDARY", String.class, "0x4090B1F0");
        addDefaultSetting("COLOR_PANEL_TEXT", String.class, "0xFFEFFAFD");
        addDefaultSetting("COLOR_PANEL_BACKGROUND", String.class, "0xFF010A0E");
    }

    private <T> void addDefaultSetting(String key, Class<T> expectedObjectClass, T defaultValue) {
        if (settings.stream().noneMatch(setting -> setting.getKey().equals(key))) {
            AdminRegistryValue newSetting = new AdminRegistryValue(key, expectedObjectClass, defaultValue);
            settings.add(newSetting);
            database.save(newSetting);
        }
    }

    private void addDefaultSetting(String key, Class<?> expectedObjectClass, Object defaultValue, Consumer<Object> onChange) {
        if (settings.stream().noneMatch(setting -> setting.getKey().equals(key))) {
            AdminRegistryValue newSetting = new AdminRegistryValue(key, expectedObjectClass, defaultValue, onChange);
            settings.add(newSetting);
            database.save(newSetting);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getSettingValue(String key) {
        for (AdminRegistryValue adminRegistryValue : settings) {
            if (adminRegistryValue.getKey().equals(key)) {
                return (T) adminRegistryValue.getValue();
            }
        }
        return null;
    }

    public AdminRegistryValue getSetting(String key) {
        for (AdminRegistryValue adminRegistryValue : settings) {
            if (adminRegistryValue.getKey().equals(key)) {
                return adminRegistryValue;
            }
        }
        return null;
    }

    public static class AdminRegistryValue {
        private final String key;
        private final Class<?> expectedObjectClass;
        private Object value;
        private final Consumer<Object> onChange;

        public AdminRegistryValue(String key, Class<?> expectedObjectClass, Object value, Consumer<Object> onChange) {
            this.key = key;
            this.expectedObjectClass = expectedObjectClass;
            this.value = value;
            this.onChange = onChange;
        }

        public AdminRegistryValue(String key, Class<?> expectedObjectClass, Object value) {
            this.key = key;
            this.expectedObjectClass = expectedObjectClass;
            this.value = value;
            this.onChange = null;
        }

        public Class<?> getExpectedObjectClass() {
            return expectedObjectClass;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
            GameOnInstance.getRegistry().database.save(this);
            if (onChange != null) {
                onChange.accept(value);
            }
        }

        // Used by json db
        public String getId() {
            return getKey();
        }
    }
}
