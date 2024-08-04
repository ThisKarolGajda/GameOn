package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface ITokenAuthenticationExtension extends IAuthenticationExtension {

    String authenticate(UserId userId);

    boolean validateToken(String token);

    UserId getUserFromToken(String token);

    String generatePairingToken(UserId userId);

    UserId validatePairingToken(String nickname, String token);
}
