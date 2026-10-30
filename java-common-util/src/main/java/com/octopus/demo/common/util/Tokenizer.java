package com.octopus.demo.common.util;

import java.util.List;

/**
 * Splits text into the token sequence used by {@link PhraseMatchUtils}.
 *
 * <p>Implementations must return a non-null list of non-null, non-empty strings.
 * Callers may supply their own tokenizer to control word boundaries, casing or
 * normalization; {@link #defaultTokenizer()} provides a zero-dependency,
 * deterministic default.</p>
 */
@FunctionalInterface
public interface Tokenizer {

    /**
     * Tokenizes the given text.
     *
     * @param text input text; may be {@code null}
     * @return token list, never {@code null}
     */
    List<String> tokenize(String text);

    /**
     * Returns the shared, stateless default tokenizer.
     *
     * @return default tokenizer singleton
     */
    static Tokenizer defaultTokenizer() {
        return SimpleTokenizer.INSTANCE;
    }
}
