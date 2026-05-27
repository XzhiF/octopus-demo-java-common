package com.octopus.demo.common.util;

/**
 * Configuration record for JwtUtil settings.
 * secretKey may be null to indicate partial update (only expirationDays changes).
 * expirationDays may be negative for testing expired tokens.
 */
public record JwtConfig(String secretKey, long expirationDays) {}