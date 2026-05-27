package com.octopus.demo.common.util;

/**
 * Configuration record for JwtUtil settings.
 * secretKey may be null to indicate partial update (only expirationDays changes).
 */
public record JwtConfig(String secretKey, long expirationDays) {

    public JwtConfig {
        if (expirationDays <= 0) {
            throw new IllegalArgumentException(
                "expirationDays must be > 0, got " + expirationDays);
        }
    }
}