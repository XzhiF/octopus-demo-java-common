package com.octopus.demo.common.util;

/**
 * Text truncation utility.
 * Counts by UTF-16 code units ({@link String#length()}); surrogate-pair boundaries
 * are not special-cased (demo precision, spec KD2).
 */
public final class TruncateUtils {

    private TruncateUtils() {}

    /**
     * Truncate {@code text} to at most {@code maxLen} characters.
     * null/empty returns as-is; maxLen <= 0 returns ""; over-long text becomes
     * the first maxLen-1 chars plus an ellipsis ("…" counts toward maxLen).
     */
    public static String truncate(String text, int maxLen) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        if (maxLen <= 0) {
            return "";
        }
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen - 1) + "…";
    }
}
