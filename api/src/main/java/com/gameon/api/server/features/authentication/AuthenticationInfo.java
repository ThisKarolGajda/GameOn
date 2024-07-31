package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModuleInfo;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import io.javalin.http.HandlerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthenticationInfo extends AbstractModuleInfo {

    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.AUTHENTICATION;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        IAuthentication authentication = (IAuthentication) extension;

        Set<HandlerData> routes = new HashSet<>();

        routes.add(new HandlerData(
                "pair",
                HandlerType.GET,
                HandlerAccessType.EVERYONE,
                ctx -> {
                    Map<String, Object> json = deserializeAndCastRequestBody(ctx);
                    String token = (String) json.get("token");
                    String nickname = (String) json.get("nickname");
                    UserId userId = authentication.validatePairingToken(nickname, token);
                    if (userId != null) {
                        String jwtToken = authentication.authenticate(userId);
                        success(ctx, Map.of("token", jwtToken));
                    } else {
                        error(ctx, "Invalid pairing token");
                    }
                }
        ));

        return routes;
    }


    @Override
    public String getDefaultPath() {
        return "authentication";
    }
}
