package com.gameon.api.server.features.permission;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModuleInfo;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PermissionInfo extends AbstractModuleInfo {
    private IPermissionExtension permission;

    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.PERMISSION;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        permission = (IPermissionExtension) extension;
        Set<HandlerData> routes = new HashSet<>();

        routes.add(new HandlerData("is-admin", HandlerType.GET, HandlerAccessType.EVERYONE, this::isAdmin));

        return routes;
    }

    private void isAdmin(Context context) {
        Map<String, Object> json = deserialize(context);
        String uuid = (String) json.get("uuid");
        String nickname = (String) json.get("nickname");
        System.out.println("isAdmin: uuid=" + uuid + " nickname=" + nickname);
        boolean isAdmin = permission.isAdmin(new UserId(UUID.fromString(uuid), nickname));
        if (isAdmin) {
            success(context, "User has admin permission");
        } else {
            error(context, "User does not have admin permission");
        }
    }

    @Override
    public String getDefaultPath() {
        return "permission";
    }
}
