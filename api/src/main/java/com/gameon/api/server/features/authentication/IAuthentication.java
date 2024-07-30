package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IAuthentication extends IExtension {

    /**
     * Authenticates a user based on their UserId.
     *
     * @param userId The UserId of the user to authenticate.
     * @return A token representing the authenticated user session.
     */
    String authenticate(UserId userId);

    /**
     * Validates a given authentication token.
     *
     * @param token The token to validate.
     * @return True if the token is valid, false otherwise.
     */
    boolean validateToken(String token);

    /**
     * Retrieves the UserId associated with a given authentication token.
     *
     * @param token The token from which to extract the UserId.
     * @return The UserId associated with the token, or null if the token is invalid.
     */
    UserId getUserFromToken(String token);
}
