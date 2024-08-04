package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import io.jsonwebtoken.Claims;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationExtension implements ITokenAuthenticationExtension {
    private final JwtTokenManager jwtTokenManager;
    private final Map<String, UserId> pairingTokens;

    public JwtAuthenticationExtension(String secretKey, long expirationTimeMillis) {
        this.jwtTokenManager = new JwtTokenManager(secretKey, expirationTimeMillis);
        this.pairingTokens = new HashMap<>();
    }

    @Override
    public String authenticate(UserId userId) {
        return jwtTokenManager.createToken(userId);
    }

    @Override
    public boolean validateToken(String token) {
        Claims claims = jwtTokenManager.parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }

    @Override
    public UserId getUserFromToken(String token) {
        Claims claims = jwtTokenManager.parseToken(token);
        if (claims != null) {
            return UserId.fromString(claims.getSubject());
        }
        return null;
    }

    @Override
    public String generatePairingToken(UserId userId) {
        String pairingToken = generateRandomToken();
        pairingTokens.put(userId.username() + ":" + pairingToken, userId);
        return pairingToken;
    }

    private String generateRandomToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }

        return token.toString();
    }

    @Override
    public UserId validatePairingToken(String nickname, String token) {
        return pairingTokens.remove(nickname + ":" + token);
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
