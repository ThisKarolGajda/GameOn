package com.gameon.api.server.features.permission;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IPermission extends IExtension {

    boolean isAdmin(UserId userId);
}
