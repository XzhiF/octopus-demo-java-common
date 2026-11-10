package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins down the normalization pipeline (NFKC -> lowercase -> strip whitespace/punctuation/
 * symbols) at its boundaries. Every case asserts a concrete hit/not-hit outcome, so no
 * input shape is left as "undefined behavior".
 */
@DisplayName("Normalization edge cases")
class NormalizationEdgeTest {

    private static final String ZERO_WIDTH_SPACE = "​";

    private final SensitiveWordFilter filter =
            new DefaultSensitiveWordFilter(SensitiveDictionaryLoader.loadBuiltIn());

    @Test
    @DisplayName("null, empty and whitespace-only input normalize to an empty string")
    void emptyInputs() {
        assertThat(SensitiveTextNormalizer.normalize(null)).isEmpty();
        assertThat(SensitiveTextNormalizer.normalize("")).isEmpty();
        assertThat(SensitiveTextNormalizer.normalize(" \t\n\r ")).isEmpty();
        assertThat(filter.check(null).hit()).isFalse();
        assertThat(filter.check(" \t\n ").hit()).isFalse();
    }

    @Test
    @DisplayName("full-width and half-width variants fold to the same normalized form")
    void fullWidthAndHalfWidth() {
        assertThat(SensitiveTextNormalizer.normalize("ＢＡＳＴＡＲＤ"))
                .isEqualTo("bastard");
        assertThat(filter.check("ＢＡＳＴＡＲＤ").hit()).isTrue();
        assertThat(filter.check("蠢 货").hit()).isTrue();
    }

    @Test
    @DisplayName("case is ignored (Locale.ROOT lowercase)")
    void mixedCase() {
        assertThat(SensitiveTextNormalizer.normalize("BaStArD")).isEqualTo("bastard");
        assertThat(filter.check("BaStArD").hit()).isTrue();
        assertThat(filter.check("IDIOT").hit()).isTrue();
    }

    @Test
    @DisplayName("Chinese punctuation is stripped between characters")
    void chinesePunctuation() {
        assertThat(SensitiveTextNormalizer.normalize("蠢，货。")).isEqualTo("蠢货");
        assertThat(filter.check("蠢，货。").hit()).isTrue();
        assertThat(filter.check("色·情").hit()).isTrue();
    }

    @Test
    @DisplayName("English punctuation and symbols are stripped between characters")
    void englishPunctuationAndSymbols() {
        assertThat(SensitiveTextNormalizer.normalize("b.a.s-t*a!r?d")).isEqualTo("bastard");
        assertThat(filter.check("b.a.s-t*a!r?d").hit()).isTrue();
        assertThat(filter.check("$p@o#r%n").hit()).isTrue();
    }

    @Test
    @DisplayName("consecutive whitespace of any kind is removed entirely")
    void consecutiveWhitespace() {
        assertThat(SensitiveTextNormalizer.normalize("蠢 \t\n 货")).isEqualTo("蠢货");
        assertThat(filter.check("蠢 \t\n 货").hit()).isTrue();
        assertThat(filter.check("b   a    s     t      a       r        d").hit()).isTrue();
    }

    @Test
    @DisplayName("separator-only input reduces to empty and never hits")
    void separatorOnlyInput() {
        assertThat(SensitiveTextNormalizer.normalize("!!!???---___…。，、")).isEmpty();
        assertThat(filter.check("!!!???---___…。，、").hit()).isFalse();
        assertThat(filter.check("!!!???---___…。，、").category()).isNull();
    }

    @Test
    @DisplayName("zero-width characters (U+200B) are NOT stripped — documented limitation")
    void zeroWidthCharactersAreNotStripped() {
        // U+200B is category Cf, outside \p{P}/\p{S}/\p{Z}/\s, so it survives normalization
        // and breaks the substring match. This is a known, accepted limitation of the MVP
        // normalizer (see README "Known limitations"); asserted here so a change is caught.
        String zeroWidthAbuse = "bast" + ZERO_WIDTH_SPACE + "ard";
        String zeroWidthChinese = "蠢" + ZERO_WIDTH_SPACE + "货";

        assertThat(SensitiveTextNormalizer.normalize(zeroWidthAbuse)).isNotEqualTo("bastard");
        assertThat(SensitiveTextNormalizer.normalize(zeroWidthAbuse)).contains(ZERO_WIDTH_SPACE);
        assertThat(filter.check(zeroWidthAbuse).hit()).isFalse();
        assertThat(filter.check(zeroWidthChinese).hit()).isFalse();
    }
}
