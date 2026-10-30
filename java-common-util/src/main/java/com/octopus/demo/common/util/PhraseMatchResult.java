package com.octopus.demo.common.util;

/**
 * Structured result of a phrase match.
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code matched} — whether the phrase matched within the allowed slop;</li>
 *   <li>{@code score} — match quality in {@code (0, 1]}, computed as {@code 1.0 / (1.0 + actualSlop)};
 *       {@code 0.0} when not matched;</li>
 *   <li>{@code actualSlop} — minimum position movement required for the best candidate;
 *       {@code -1} when not matched;</li>
 *   <li>{@code startToken} — first document token index of the matched span, inclusive;
 *       {@code -1} when not matched;</li>
 *   <li>{@code endToken} — last document token index of the matched span, inclusive;
 *       {@code -1} when not matched.</li>
 * </ul>
 *
 * <p>Sentinel for a miss: {@code (false, 0.0, -1, -1, -1)}, see {@link #noMatch()}.</p>
 */
public record PhraseMatchResult(
        boolean matched,
        double score,
        int actualSlop,
        int startToken,
        int endToken) {

    public PhraseMatchResult {
        if (matched) {
            if (actualSlop < 0) {
                throw new IllegalArgumentException("actualSlop must be >= 0 when matched");
            }
            if (!(score > 0.0 && score <= 1.0)) {
                throw new IllegalArgumentException("score must be in (0, 1] when matched");
            }
            if (startToken < 0 || startToken > endToken) {
                throw new IllegalArgumentException("startToken must satisfy 0 <= startToken <= endToken");
            }
        } else {
            if (score != 0.0 || actualSlop != -1 || startToken != -1 || endToken != -1) {
                throw new IllegalArgumentException(
                        "unmatched result must be (false, 0.0, -1, -1, -1)");
            }
        }
    }

    /**
     * Returns the canonical miss result {@code (false, 0.0, -1, -1, -1)}.
     */
    public static PhraseMatchResult noMatch() {
        return new PhraseMatchResult(false, 0.0, -1, -1, -1);
    }

    /**
     * Creates a matched result; the score is derived as {@code 1.0 / (1.0 + actualSlop)}.
     *
     * @param actualSlop minimum position movement of the best candidate, must be {@code >= 0}
     * @param startToken first document token index of the span, inclusive
     * @param endToken   last document token index of the span, inclusive
     * @return a matched result
     * @throws IllegalArgumentException if {@code startToken > endToken}
     */
    public static PhraseMatchResult match(int actualSlop, int startToken, int endToken) {
        return new PhraseMatchResult(true, 1.0 / (1.0 + actualSlop), actualSlop, startToken, endToken);
    }
}
