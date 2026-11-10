package com.octopus.demo.common.sensitive;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Normalizes text before matching: NFKC → lowercase (Locale.ROOT) → strip Unicode
 * whitespace, punctuation and symbols.
 */
public final class SensitiveTextNormalizer {

    private SensitiveTextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String nfkc = Normalizer.normalize(text, Normalizer.Form.NFKC);
        String lower = nfkc.toLowerCase(Locale.ROOT);
        return lower.replaceAll("[\\p{P}\\p{S}\\p{Z}\\s]", "");
    }
}
