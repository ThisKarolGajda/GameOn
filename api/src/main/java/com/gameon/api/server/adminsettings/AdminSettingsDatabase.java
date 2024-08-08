package com.gameon.api.server.adminsettings;

import com.gameon.api.server.database.JSONDatabase;

public class AdminSettingsDatabase extends JSONDatabase<String, AdminSettingsRegistry.AdminRegistryValue> {
    public AdminSettingsDatabase() {
        super("admin-settings", AdminSettingsRegistry.AdminRegistryValue[].class, false);
    }
}
