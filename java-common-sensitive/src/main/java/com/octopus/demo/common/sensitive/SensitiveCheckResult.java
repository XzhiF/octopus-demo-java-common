package com.octopus.demo.common.sensitive;

/**
 * Result of a sensitive word check.
 * {@code category} is {@code null} when {@code hit} is {@code false}.
 */
public record SensitiveCheckResult(boolean hit, SensitiveCategory category) {

    public static SensitiveCheckResult hit(SensitiveCategory category) {
        return new SensitiveCheckResult(true, category);
    }

    public static SensitiveCheckResult notHit() {
        return new SensitiveCheckResult(false, null);
    }
}
