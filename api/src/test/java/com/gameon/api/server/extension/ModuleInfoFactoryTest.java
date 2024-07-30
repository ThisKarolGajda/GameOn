package com.gameon.api.server.extension;

import com.gameon.api.server.features.GameOnFeatureType;
import com.gameon.api.server.features.authentication.AuthenticationInfo;
import com.gameon.api.server.features.economy.EconomyInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuleInfoFactoryTest {

    @Test
    void testCreateAuthenticationInfo() {
        IModuleInfo moduleInfo = ModuleInfoFactory.create(GameOnFeatureType.AUTHENTICATION);
        assertNotNull(moduleInfo);
        assertInstanceOf(AuthenticationInfo.class, moduleInfo, "Expected instance of AuthenticationInfo");
    }

    @Test
    void testCreateEconomyInfo() {
        IModuleInfo moduleInfo = ModuleInfoFactory.create(GameOnFeatureType.ECONOMY);
        assertNotNull(moduleInfo);
        assertInstanceOf(EconomyInfo.class, moduleInfo, "Expected instance of EconomyInfo");
    }

    @Test
    void testCreateInvalidFeatureType() {
        assertThrows(IllegalArgumentException.class, () -> ModuleInfoFactory.create(null));
    }
}
