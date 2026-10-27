package com.octopus.demo.common.sensitive;

/**
 * Filter that never reports a hit. Registered when {@code octopus.sensitive.enabled=false}
 * so downstream beans can still inject {@link SensitiveWordFilter}.
 */
public enum NoOpSensitiveWordFilter implements SensitiveWordFilter {
    INSTANCE;

    @Override
    public SensitiveCheckResult check(String text) {
        return SensitiveCheckResult.notHit();
    }
}
