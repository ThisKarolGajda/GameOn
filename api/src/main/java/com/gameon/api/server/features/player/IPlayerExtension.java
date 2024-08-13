package com.gameon.api.server.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IPlayerExtension extends IExtension {

    IPlayer getPlayer(UserId userId);

    void savePlayer(IPlayer player);
}
