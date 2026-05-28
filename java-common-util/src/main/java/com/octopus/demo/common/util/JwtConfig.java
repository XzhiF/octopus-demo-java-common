package com.octopus.demo.common.util;

/**
 * Configuration record for JwtUtil settings.
 * secretKey may be null to indicate partial update (only expirationDays changes).
 * expirationDays must be > 0 — use raw JJWT API for testing expired tokens.
 */
public record JwtConfig(String secretKey, long expirationDays) {

    public JwtConfig {
        if (expirationDays <= 0) {
            throw new IllegalArgumentException(
                "expirationDays must be > 0, got " + expirationDays);
        }
    }
}