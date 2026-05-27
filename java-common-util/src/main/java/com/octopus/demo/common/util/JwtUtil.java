package com.octopus.demo.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * JWT generation and parsing utility using HMAC-SHA256.
 * Pure static utility — configure via update(JwtConfig), use via static methods.
 * Default expiration: 30 days.
 *
 * WARNING: The default secret key is for development/testing only.
 * Production environments MUST call update(JwtConfig) with a strong, unique key.
 */
public final class JwtUtil {

    private static volatile String secretKey = "octopus-jwt-secret-key-default!!";
    private static volatile long expirationDays = 30;
    private static volatile SecretKey cachedSigningKey = deriveKey(secretKey);

    private JwtUtil() {}

    /**
     * Updates JWT configuration. Synchronized for thread-safe writes.
     * secretKey may be null in JwtConfig to indicate no key change.
     * expirationDays must be > 0 — enforced by JwtConfig record validation.
     */
    public static synchronized void update(JwtConfig config) {
        Objects.requireNonNull(config, "JwtConfig must not be null");
        if (config.secretKey() != null) {
            validateKeyLength(config.secretKey());
            secretKey = config.secretKey();
            cachedSigningKey = deriveKey(config.secretKey());
        }
        expirationDays = config.expirationDays();
    }

    public static String generateToken(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0");
        }
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + expirationDays * 24 * 60 * 60 * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(cachedSigningKey)
                .compact();
    }

    public static Long parseToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(cachedSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException(e);
        } catch (NumberFormatException e) {
            throw new JwtTokenInvalidException("Invalid userId in token", e);
        } catch (Exception e) {
            throw new JwtTokenInvalidException("JWT token invalid", e);
        }
    }

    public static boolean isTokenExpired(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(cachedSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new JwtTokenInvalidException("JWT token invalid", e);
        }
    }

    public static String generateRandomSecretKeyString() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    private static SecretKey deriveKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    private static void validateKeyLength(String key) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                "Secret key must be at least 32 UTF-8 bytes for HMAC-SHA256, got " + bytes.length);
        }
    }
}