package com.octopus.demo.common.util;

/**
 * String utility methods.
 * Note: deliberately NOT commons-lang3's StringUtils — no wildcard imports of it here.
 */
public final class StringUtils {

    private StringUtils() {}

    /**
     * Converts the first character (by code point) to titlecase, leaving the rest unchanged.
     * Handles supplementary characters (surrogate pairs) and special titlecase mappings
     * (e.g. digraph ǆ → titlecase form) without breaking the pair.
     *
     * @return null for null, "" for empty, otherwise first code point titlecased
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int cp = str.codePointAt(0);
        int cpLen = Character.charCount(cp);
        return new String(Character.toChars(Character.toTitleCase(cp))) + str.substring(cpLen);
    }
}
