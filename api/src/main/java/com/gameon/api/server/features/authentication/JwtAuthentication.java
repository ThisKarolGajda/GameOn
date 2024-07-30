package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import io.jsonwebtoken.Claims;

import java.util.Date;

public class JwtAuthentication implements IAuthentication {
    private final JwtTokenManager jwtTokenManager;

    public JwtAuthentication(String secretKey, long expirationTimeMillis) {
        this.jwtTokenManager = new JwtTokenManager(secretKey, expirationTimeMillis);
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
    public boolean canBeUsed() {
        return true;
    }
}
