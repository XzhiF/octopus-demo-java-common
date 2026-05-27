package com.octopus.demo.common.util;

import java.nio.charset.StandardCharsets;

/**
 * Configuration record for AEDUtils settings.
 * Fields may be null to indicate partial update (only non-null fields are applied).
 * Non-null fields are validated at construction time for correct byte length.
 */
public record AEDConfig(String secretKey, String ivParameter) {

    public AEDConfig {
        if (secretKey != null) {
            int actual = secretKey.getBytes(StandardCharsets.UTF_8).length;
            if (actual != 16) {
                throw new IllegalArgumentException(
                    "Secret key must be exactly 16 UTF-8 bytes for AES-128, got " + actual);
            }
        }
        if (ivParameter != null) {
            int actual = ivParameter.getBytes(StandardCharsets.UTF_8).length;
            if (actual != 16) {
                throw new IllegalArgumentException(
                    "IV parameter must be exactly 16 UTF-8 bytes, got " + actual);
            }
        }
    }
}