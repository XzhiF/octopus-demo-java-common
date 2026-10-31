package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Elasticsearch/Lucene-style phrase matching over token sequences.
 *
 * <p>A phrase matches when every phrase token can be assigned a distinct document
 * position and the total positional movement stays within the allowed {@code slop}.
 * Unlike unordered term matching, order matters: moving terms around costs slop,
 * so transposed terms are <em>not</em> a slop-zero match.</p>
 *
 * <p>Examples (default tokenizer):</p>
 * <pre>{@code
 * // exact phrase: actualSlop 0, score 1.0, span 1..3
 * PhraseMatchUtils.match("the quick brown fox", "quick brown fox", 0);
 *
 * // one intervening token: actualSlop 1, score 0.5, span 0..3
 * PhraseMatchUtils.match("quick very brown fox", "quick brown fox", 1);
 *
 * // transposition needs slop 2: matched with slop 2, not with slop 1
 * PhraseMatchUtils.match("quick fox brown", "quick brown fox", 2);
 * }</pre>
 *
 * <p>The default tokenizer splits on any non letter/digit/combining-mark character,
 * lowercases latin runs, and emits each CJK (Han/Hiragana/Katakana/Hangul) code point
 * as its own token. CJK word-level segmentation therefore requires a custom
 * {@link Tokenizer}.</p>
 *
 * <p>The class is stateless and thread-safe; input lists are never modified.</p>
 */
public final class PhraseMatchUtils {

    private static final long INF = Long.MAX_VALUE / 4;

    private PhraseMatchUtils() {
    }

    /**
     * Matches {@code phrase} against {@code text} using the default tokenizer.
     *
     * @param text   document text; {@code null} yields {@link PhraseMatchResult#noMatch()}
     * @param phrase phrase text; {@code null} yields {@link PhraseMatchResult#noMatch()}
     * @param slop   maximum allowed positional movement, must be {@code >= 0}
     * @return structured match result
     * @throws IllegalArgumentException if {@code slop < 0}
     */
    public static PhraseMatchResult match(String text, String phrase, int slop) {
        return match(text, phrase, slop, Tokenizer.defaultTokenizer());
    }

    /**
     * Matches {@code phrase} against {@code text} using the supplied tokenizer.
     *
     * @param text      document text; {@code null} yields {@link PhraseMatchResult#noMatch()}
     * @param phrase    phrase text; {@code null} yields {@link PhraseMatchResult#noMatch()}
     * @param slop      maximum allowed positional movement, must be {@code >= 0}
     * @param tokenizer tokenizer applied to both inputs
     * @return structured match result
     * @throws NullPointerException     if {@code tokenizer} is {@code null}
     * @throws IllegalArgumentException if {@code slop < 0}
     */
    public static PhraseMatchResult match(String text, String phrase, int slop, Tokenizer tokenizer) {
        Objects.requireNonNull(tokenizer, "tokenizer must not be null");
        requireNonNegativeSlop(slop);
        if (text == null || phrase == null) {
            return PhraseMatchResult.noMatch();
        }
        return matchTokens(tokenizer.tokenize(text), tokenizer.tokenize(phrase), slop);
    }

    /**
     * Matches pre-tokenized inputs. Equivalent to the string overloads but skips
     * tokenization, so callers can reuse token sequences.
     *
     * @param documentTokens document tokens, positions are their list indices
     * @param phraseTokens   phrase tokens in required order
     * @param slop           maximum allowed positional movement, must be {@code >= 0}
     * @return structured match result
     * @throws IllegalArgumentException if {@code slop < 0} or a token is {@code null}/empty
     */
    public static PhraseMatchResult matchTokens(List<String> documentTokens,
                                                List<String> phraseTokens,
                                                int slop) {
        requireNonNegativeSlop(slop);
        if (documentTokens == null || phraseTokens == null) {
            return PhraseMatchResult.noMatch();
        }
        if (documentTokens.isEmpty() || phraseTokens.isEmpty()) {
            return PhraseMatchResult.noMatch();
        }
        validateTokens(documentTokens);
        validateTokens(phraseTokens);

        int n = documentTokens.size();
        int k = phraseTokens.size();
        if (n < k) {
            return PhraseMatchResult.noMatch();
        }

        Map<String, List<Integer>> phraseGroups = new TreeMap<>();
        for (int i = 0; i < k; i++) {
            phraseGroups.computeIfAbsent(phraseTokens.get(i), key -> new ArrayList<>()).add(i);
        }
        Map<String, List<Integer>> documentPositions = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            documentPositions.computeIfAbsent(documentTokens.get(i), key -> new ArrayList<>()).add(i);
        }

        TreeSet<Integer> candidates = new TreeSet<>();
        for (int pos = 0; pos < n; pos++) {
            for (int i = 0; i < k; i++) {
                candidates.add(pos - i);
            }
        }

        long bestCost = INF;
        int bestStart = Integer.MAX_VALUE;
        int bestEnd = Integer.MAX_VALUE;
        for (int t : candidates) {
            long total = 0L;
            int spanStart = Integer.MAX_VALUE;
            int spanEnd = Integer.MIN_VALUE;
            boolean feasible = true;
            for (Map.Entry<String, List<Integer>> entry : phraseGroups.entrySet()) {
                List<Integer> phraseIndices = entry.getValue();
                List<Integer> occurrences = documentPositions.get(entry.getKey());
                if (occurrences == null || occurrences.size() < phraseIndices.size()) {
                    feasible = false;
                    break;
                }
                int[] targets = new int[phraseIndices.size()];
                for (int g = 0; g < targets.length; g++) {
                    targets[g] = t + phraseIndices.get(g);
                }
                GroupState state = groupState(occurrences, targets);
                if (state == null) {
                    feasible = false;
                    break;
                }
                total = Math.min(INF, total + state.cost);
                spanStart = Math.min(spanStart, state.minPos);
                spanEnd = Math.max(spanEnd, state.maxPos);
            }
            if (!feasible) {
                continue;
            }
            if (total < bestCost
                    || (total == bestCost
                        && (spanStart < bestStart
                            || (spanStart == bestStart && spanEnd < bestEnd)))) {
                bestCost = total;
                bestStart = spanStart;
                bestEnd = spanEnd;
            }
        }

        if (bestCost == INF || bestCost > slop) {
            return PhraseMatchResult.noMatch();
        }
        return PhraseMatchResult.match((int) bestCost, bestStart, bestEnd);
    }

    /**
     * Computes the minimum-cost assignment of the given targets to distinct
     * occurrences (both ascending), with a leftmost tie-break on the resulting span.
     *
     * @return best state, or {@code null} when fewer occurrences than targets exist
     */
    private static GroupState groupState(List<Integer> occurrences, int[] targets) {
        int n = occurrences.size();
        int m = targets.length;
        if (n < m) {
            return null;
        }
        GroupState[][] dp = new GroupState[n + 1][m + 1];
        for (int j = 0; j <= n; j++) {
            dp[j][0] = GroupState.EMPTY;
        }
        for (int j = 1; j <= n; j++) {
            int pos = occurrences.get(j - 1);
            int maxTargets = Math.min(j, m);
            for (int g = 1; g <= maxTargets; g++) {
                GroupState best = dp[j - 1][g];
                GroupState prev = dp[j - 1][g - 1];
                if (prev != null) {
                    long cost = Math.min(INF, prev.cost + Math.abs((long) pos - targets[g - 1]));
                    GroupState taken = new GroupState(
                            cost,
                            Math.min(prev.minPos, pos),
                            Math.max(prev.maxPos, pos));
                    if (best == null || taken.compareTo(best) < 0) {
                        best = taken;
                    }
                }
                dp[j][g] = best;
            }
        }
        return dp[n][m];
    }

    private static void requireNonNegativeSlop(int slop) {
        if (slop < 0) {
            throw new IllegalArgumentException("slop must be >= 0");
        }
    }

    private static void validateTokens(List<String> tokens) {
        for (String token : tokens) {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("tokens must be non-null and non-empty");
            }
        }
    }

    /** Immutable per-group match state: cost plus the span it occupies. */
    private record GroupState(long cost, int minPos, int maxPos) {

        static final GroupState EMPTY = new GroupState(0L, Integer.MAX_VALUE, Integer.MIN_VALUE);

        int compareTo(GroupState other) {
            int byCost = Long.compare(cost, other.cost);
            if (byCost != 0) {
                return byCost;
            }
            int byMin = Integer.compare(minPos, other.minPos);
            if (byMin != 0) {
                return byMin;
            }
            return Integer.compare(maxPos, other.maxPos);
        }
    }
}
