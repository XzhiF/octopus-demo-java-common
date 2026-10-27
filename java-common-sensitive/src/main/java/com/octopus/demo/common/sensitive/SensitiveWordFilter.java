package com.octopus.demo.common.sensitive;

/**
 * Checks free text for sensitive words.
 */
public interface SensitiveWordFilter {

    /**
     * @param text text to check; {@code null} or blank yields a not-hit result
     * @return the check result, never {@code null}
     */
    SensitiveCheckResult check(String text);
}
