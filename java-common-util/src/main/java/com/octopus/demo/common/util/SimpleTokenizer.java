package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deterministic, zero-dependency default tokenizer.
 *
 * <p>Rules:</p>
 * <ul>
 *   <li>Combining marks are appended to the current buffer and never split it.</li>
 *   <li>Letters/digits in Han, Hiragana, Katakana or Hangul scripts form a
 *       single-code-point token each (CJK is tokenized per character).</li>
 *   <li>Other letters/digits accumulate into one token, lower-cased with
 *       {@link Locale#ROOT}.</li>
 *   <li>Anything else (whitespace, punctuation, symbols, emoji) separates tokens.</li>
 * </ul>
 *
 * <p>Stateless and thread-safe.</p>
 */
final class SimpleTokenizer implements Tokenizer {

    static final SimpleTokenizer INSTANCE = new SimpleTokenizer();

    private SimpleTokenizer() {}

    @Override
    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return tokens;
        }
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            int type = Character.getType(codePoint);
            if (isCombiningMark(type)) {
                buffer.appendCodePoint(codePoint);
            } else if (Character.isLetterOrDigit(codePoint) && isCjk(codePoint)) {
                flush(buffer, tokens);
                tokens.add(new String(Character.toChars(codePoint)));
            } else if (Character.isLetterOrDigit(codePoint)) {
                buffer.appendCodePoint(codePoint);
            } else {
                flush(buffer, tokens);
            }
            index += Character.charCount(codePoint);
        }
        flush(buffer, tokens);
        return tokens;
    }

    private static boolean isCombiningMark(int type) {
        return type == Character.NON_SPACING_MARK
            || type == Character.COMBINING_SPACING_MARK
            || type == Character.ENCLOSING_MARK;
    }

    private static boolean isCjk(int codePoint) {
        Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN
            || script == Character.UnicodeScript.HIRAGANA
            || script == Character.UnicodeScript.KATAKANA
            || script == Character.UnicodeScript.HANGUL;
    }

    private static void flush(StringBuilder buffer, List<String> tokens) {
        if (buffer.length() > 0) {
            tokens.add(buffer.toString().toLowerCase(Locale.ROOT));
            buffer.setLength(0);
        }
    }
}
