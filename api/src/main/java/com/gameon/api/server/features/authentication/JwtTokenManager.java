package com.gameon.api.server.features.authentication;

import com.gameon.api.server.common.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class JwtTokenManager {
    private final String secretKey;
    private final long expirationTimeMillis;

    public JwtTokenManager(String secretKey, long expirationTimeMillis) {
        this.secretKey = secretKey;
        this.expirationTimeMillis = expirationTimeMillis;
    }

    public String createToken(@NotNull UserId userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ignored) {
            return null;
        }
    }

}
