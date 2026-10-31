package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class PhraseMatchResultTest {

    @Test
    @DisplayName("noMatch() returns the exact sentinel tuple")
    void noMatch_sentinel() {
        PhraseMatchResult result = PhraseMatchResult.noMatch();
        assertThat(result.matched()).isFalse();
        assertThat(result.score()).isEqualTo(0.0);
        assertThat(result.actualSlop()).isEqualTo(-1);
        assertThat(result.startToken()).isEqualTo(-1);
        assertThat(result.endToken()).isEqualTo(-1);
    }

    @Test
    @DisplayName("match(0, 1, 3) -> exact phrase, score 1.0")
    void match_exactPhrase() {
        PhraseMatchResult result = PhraseMatchResult.match(0, 1, 3);
        assertThat(result.matched()).isTrue();
        assertThat(result.actualSlop()).isEqualTo(0);
        assertThat(result.score()).isEqualTo(1.0);
        assertThat(result.startToken()).isEqualTo(1);
        assertThat(result.endToken()).isEqualTo(3);
    }

    @Test
    @DisplayName("match(1, 0, 3) -> score 0.5")
    void match_slopOne() {
        PhraseMatchResult result = PhraseMatchResult.match(1, 0, 3);
        assertThat(result.matched()).isTrue();
        assertThat(result.score()).isCloseTo(0.5, within(1e-9));
    }

    @Test
    @DisplayName("match(2, 0, 2) -> score 1/3")
    void match_slopTwo() {
        PhraseMatchResult result = PhraseMatchResult.match(2, 0, 2);
        assertThat(result.matched()).isTrue();
        assertThat(result.actualSlop()).isEqualTo(2);
        assertThat(result.score()).isCloseTo(1.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("matched result with negative actualSlop is rejected")
    void constructor_matchedNegativeSlop() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, 0.5, -1, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result with score 0 is rejected")
    void constructor_matchedScoreZero() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, 0.0, 0, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result with score greater than 1 is rejected")
    void constructor_matchedScoreAboveOne() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, 1.5, 0, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result with negative score is rejected")
    void constructor_matchedScoreNegative() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, -0.1, 0, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result with startToken > endToken is rejected")
    void constructor_matchedStartAfterEnd() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, 0.5, 0, 3, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result with negative startToken is rejected")
    void constructor_matchedNegativeStart() {
        assertThatThrownBy(() -> new PhraseMatchResult(true, 0.5, 0, -1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("matched result allows startToken == endToken (single token span)")
    void constructor_matchedSingleTokenSpan() {
        assertThatCode(() -> new PhraseMatchResult(true, 1.0, 0, 2, 2))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("unmatched result with non-sentinel score is rejected")
    void constructor_unmatchedNonZeroScore() {
        assertThatThrownBy(() -> new PhraseMatchResult(false, 0.5, -1, -1, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("unmatched result with non-sentinel actualSlop is rejected")
    void constructor_unmatchedNonSentinelSlop() {
        assertThatThrownBy(() -> new PhraseMatchResult(false, 0.0, 0, -1, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("unmatched result with non-sentinel startToken is rejected")
    void constructor_unmatchedNonSentinelStart() {
        assertThatThrownBy(() -> new PhraseMatchResult(false, 0.0, -1, 0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("unmatched result with non-sentinel endToken is rejected")
    void constructor_unmatchedNonSentinelEnd() {
        assertThatThrownBy(() -> new PhraseMatchResult(false, 0.0, -1, -1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("record equality and accessors follow record semantics")
    void record_equality() {
        assertThat(PhraseMatchResult.match(2, 0, 2))
                .isEqualTo(new PhraseMatchResult(true, 1.0 / 3.0, 2, 0, 2))
                .hasSameHashCodeAs(new PhraseMatchResult(true, 1.0 / 3.0, 2, 0, 2));
        assertThat(PhraseMatchResult.noMatch()).isEqualTo(PhraseMatchResult.noMatch());
    }
}
