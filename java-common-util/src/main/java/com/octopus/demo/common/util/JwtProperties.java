package com.octopus.demo.common.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT settings.
 * Prefix: octopus.jwt
 */
@ConfigurationProperties(prefix = "octopus.jwt")
public class JwtProperties {

    private String secretKey = "";
    private long expirationDays = 30;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(long expirationDays) {
        this.expirationDays = expirationDays;
    }
}