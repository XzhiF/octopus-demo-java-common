package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhraseMatchUtilsTest {

    private static final double EPS = 1e-9;

    // ---------------------------------------------------------------- AC1/AC2

    @Test
    @DisplayName("exact phrase matches with slop 0")
    void exactPhrase_slop0() {
        PhraseMatchResult r = PhraseMatchUtils.match("the quick brown fox", "quick brown fox", 0);

        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isZero();
        assertThat(r.score()).isEqualTo(1.0, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.startToken()).isEqualTo(1);
        assertThat(r.endToken()).isEqualTo(3);
    }

    @Test
    @DisplayName("one intervening token matches with slop 1")
    void oneGap_slop1() {
        PhraseMatchResult r = PhraseMatchUtils.match("quick very brown fox", "quick brown fox", 1);

        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isEqualTo(1);
        assertThat(r.score()).isEqualTo(0.5, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(3);
    }

    @Test
    @DisplayName("one intervening token does not match with slop 0")
    void oneGap_slop0_noMatch() {
        assertThat(PhraseMatchUtils.match("quick very brown fox", "quick brown fox", 0))
            .isEqualTo(PhraseMatchResult.noMatch());
    }

    @Test
    @DisplayName("transposed terms require slop 2")
    void transposition_requiresSlop2() {
        assertThat(PhraseMatchUtils.match("quick fox brown", "quick brown fox", 1).matched())
            .isFalse();

        PhraseMatchResult r = PhraseMatchUtils.match("quick fox brown", "quick brown fox", 2);
        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isEqualTo(2);
        assertThat(r.score()).isEqualTo(1.0 / 3.0, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(2);
    }

    @Test
    @DisplayName("two gaps need slop 3")
    void twoGaps_slop2() {
        assertThat(PhraseMatchUtils.match("quick x y brown z fox", "quick brown fox", 2).matched())
            .isFalse();

        PhraseMatchResult r = PhraseMatchUtils.match("quick x y brown z fox", "quick brown fox", 3);
        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isEqualTo(3);
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(5);
    }

    // ---------------------------------------------------------------- AC3 leftmost

    @Test
    @DisplayName("repeated terms take the leftmost span on ties")
    void repeatedTerms_leftmost() {
        PhraseMatchResult r = PhraseMatchUtils.match("a a a", "a a", 0);

        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isZero();
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(1);
    }

    @Test
    @DisplayName("repeated terms with a gap need slop 1")
    void repeatedTerms_withGap() {
        PhraseMatchResult r = PhraseMatchUtils.match("a x a", "a a", 1);

        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isEqualTo(1);
        assertThat(r.score()).isEqualTo(0.5, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(2);
    }

    @Test
    @DisplayName("repeated term cannot reuse a single position")
    void repeatedTerms_notEnoughOccurrences() {
        assertThat(PhraseMatchUtils.match("a", "a a", 5).matched()).isFalse();
    }

    // ---------------------------------------------------------------- boundaries

    @Test
    @DisplayName("phrase longer than text does not match")
    void phraseLongerThanText() {
        assertThat(PhraseMatchUtils.match("a", "a b", 5)).isEqualTo(PhraseMatchResult.noMatch());
    }

    @Test
    @DisplayName("empty phrase does not match")
    void emptyPhrase_noMatch() {
        assertThat(PhraseMatchUtils.match("abc", "", 0)).isEqualTo(PhraseMatchResult.noMatch());
    }

    @Test
    @DisplayName("null text does not match")
    void nullText_noMatch() {
        assertThat(PhraseMatchUtils.match(null, "x", 0)).isEqualTo(PhraseMatchResult.noMatch());
    }

    @Test
    @DisplayName("null phrase does not match")
    void nullPhrase_noMatch() {
        assertThat(PhraseMatchUtils.match("x", null, 0)).isEqualTo(PhraseMatchResult.noMatch());
    }

    // ---------------------------------------------------------------- AC5 input contract

    @Test
    @DisplayName("negative slop throws IllegalArgumentException")
    void negativeSlop_throws() {
        assertThatThrownBy(() -> PhraseMatchUtils.match("x", "x", -1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PhraseMatchUtils.matchTokens(List.of("x"), List.of("x"), -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null tokenizer throws NullPointerException")
    void nullTokenizer_throws() {
        assertThatThrownBy(() -> PhraseMatchUtils.match("x", "x", 0, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("null or empty tokens throw IllegalArgumentException")
    void matchTokens_nullElement_throws() {
        assertThatThrownBy(() -> PhraseMatchUtils.matchTokens(Arrays.asList("a", null), List.of("a"), 0))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PhraseMatchUtils.matchTokens(List.of("a", ""), List.of("a"), 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null or empty token lists do not match")
    void matchTokens_nullOrEmptyLists_noMatch() {
        assertThat(PhraseMatchUtils.matchTokens(null, List.of("a"), 0)).isEqualTo(PhraseMatchResult.noMatch());
        assertThat(PhraseMatchUtils.matchTokens(List.of("a"), null, 0)).isEqualTo(PhraseMatchResult.noMatch());
        assertThat(PhraseMatchUtils.matchTokens(List.of(), List.of("a"), 0)).isEqualTo(PhraseMatchResult.noMatch());
    }

    // ---------------------------------------------------------------- AC6 no mutation

    @Test
    @DisplayName("input token lists are not mutated")
    void inputListsNotMutated() {
        List<String> document = new ArrayList<>(List.of("the", "quick", "brown", "fox"));
        List<String> phrase = new ArrayList<>(List.of("quick", "brown", "fox"));

        PhraseMatchResult r = PhraseMatchUtils.matchTokens(document, phrase, 0);

        assertThat(r.matched()).isTrue();
        assertThat(document).containsExactly("the", "quick", "brown", "fox");
        assertThat(phrase).containsExactly("quick", "brown", "fox");
    }

    // ---------------------------------------------------------------- AC1 custom tokenizer

    @Test
    @DisplayName("custom tokenizer controls casing")
    void customTokenizer_caseSensitive() {
        Tokenizer caseSensitive = text -> text == null
            ? List.of()
            : Arrays.stream(text.trim().split("\\s+")).filter(s -> !s.isEmpty()).toList();

        assertThat(PhraseMatchUtils.match("Hello World", "Hello World", 0, caseSensitive).matched())
            .isTrue();
        assertThat(PhraseMatchUtils.match("Hello World", "hello world", 0, caseSensitive).matched())
            .isFalse();
    }

    // ---------------------------------------------------------------- AC4 score/span formula

    @Test
    @DisplayName("score always equals 1/(1+actualSlop) and span is ordered")
    void scoreFormula() {
        String[][] cases = {
            {"the quick brown fox", "quick brown fox", "0"},
            {"quick very brown fox", "quick brown fox", "1"},
            {"quick fox brown", "quick brown fox", "2"},
            {"a x a", "a a", "1"},
            {"a a a", "a a", "0"},
        };
        for (String[] c : cases) {
            PhraseMatchResult r = PhraseMatchUtils.match(c[0], c[1], Integer.parseInt(c[2]));
            assertThat(r.matched()).as("case %s", Arrays.toString(c)).isTrue();
            assertThat(r.score())
                .as("case %s", Arrays.toString(c))
                .isEqualTo(1.0 / (1.0 + r.actualSlop()), org.assertj.core.data.Offset.offset(EPS));
            assertThat(r.startToken()).isLessThanOrEqualTo(r.endToken());
            assertThat(r.startToken()).isGreaterThanOrEqualTo(0);
        }
    }

    // ---------------------------------------------------------------- integration with 01

    @Test
    @DisplayName("default tokenizer emits CJK characters one by one")
    void cjkDefaultTokenizer() {
        PhraseMatchResult r = PhraseMatchUtils.match("中文测试", "中文", 0);

        assertThat(r.matched()).isTrue();
        assertThat(r.actualSlop()).isZero();
        assertThat(r.startToken()).isZero();
        assertThat(r.endToken()).isEqualTo(1);
    }
}
