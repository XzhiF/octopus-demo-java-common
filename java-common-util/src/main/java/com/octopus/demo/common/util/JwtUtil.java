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

/**
 * JWT generation and parsing utility using HMAC-SHA256.
 * Immutable instance — create via static factory methods.
 */
public final class JwtUtil {

    private static final long DEFAULT_EXPIRATION_DAYS = 30;
    private static final int RANDOM_KEY_BYTE_LENGTH = 32;

    private final SecretKey secretKey;
    private final long expirationDays;

    private JwtUtil(SecretKey secretKey, long expirationDays) {
        this.secretKey = secretKey;
        this.expirationDays = expirationDays;
    }

    /**
     * Creates a JwtUtil with a random 256-bit secret key and 30-day default expiration.
     * Each invocation generates a different random key — use create(secretKey, days)
     * for production scenarios requiring fixed keys.
     */
    public static JwtUtil createDefault() {
        return create(generateRandomSecretKeyString(), DEFAULT_EXPIRATION_DAYS);
    }

    /**
     * Creates a JwtUtil with the specified secret key string and expiration days.
     * The key string must be at least 32 UTF-8 bytes for HMAC-SHA256.
     */
    public static JwtUtil create(String secretKey, long expirationDays) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key must not be null or empty");
        }
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 UTF-8 bytes for HMAC-SHA256");
        }
        return new JwtUtil(Keys.hmacShaKeyFor(keyBytes), expirationDays);
    }

    /**
     * Generates a JWT token containing the given userId in the "sub" claim.
     * @param userId must be > 0
     */
    public String generateToken(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0");
        }
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration;
        if (expirationDays <= 0) {
            expiration = new Date(nowMillis - 1000);
        } else {
            expiration = new Date(nowMillis + expirationDays * 24 * 60 * 60 * 1000L);
        }

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Parses a JWT token and returns the userId from the "sub" claim.
     * @throws JwtTokenExpiredException if the token has expired
     * @throws JwtTokenInvalidException if the token is malformed or has invalid signature
     * @throws IllegalArgumentException if token is null or empty
     */
    public Long parseToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
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

    /**
     * Checks if a JWT token has expired.
     * @throws JwtTokenInvalidException if the token is malformed
     */
    public boolean isTokenExpired(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
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

    /**
     * Generates a random 256-bit secret key as a Base64-encoded string.
     * Suitable for passing to create() or configuring in application properties.
     */
    static String generateRandomSecretKeyString() {
        byte[] keyBytes = new byte[RANDOM_KEY_BYTE_LENGTH];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
