package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DefaultSensitiveWordFilter")
class DefaultSensitiveWordFilterTest {

    private final SensitiveWordFilter filter =
            new DefaultSensitiveWordFilter(SensitiveDictionaryLoader.loadBuiltIn());

    @Test
    @DisplayName("hits built-in ABUSE and PORN words with the correct category")
    void hitsBuiltInWords() {
        assertThat(filter.check("you bastard").category()).isEqualTo(SensitiveCategory.ABUSE);
        assertThat(filter.check("这是一个色情网站").category()).isEqualTo(SensitiveCategory.PORN);
    }

    @Test
    @DisplayName("detects the E2E abuse term 蠢货")
    void hitsChineseAbuseTerm() {
        SensitiveCheckResult result = filter.check("蠢货");
        assertThat(result.hit()).isTrue();
        assertThat(result.category()).isEqualTo(SensitiveCategory.ABUSE);
    }

    @Test
    @DisplayName("ignores case")
    void caseInsensitive() {
        assertThat(filter.check("BaStArD").hit()).isTrue();
    }

    @Test
    @DisplayName("normalizes full-width characters")
    void fullWidthVariant() {
        assertThat(filter.check("ＢＡＳＴＡＲＤ").hit()).isTrue();
    }

    @Test
    @DisplayName("ignores whitespace inserted between characters")
    void whitespaceVariant() {
        assertThat(filter.check("蠢 货").hit()).isTrue();
        assertThat(filter.check("b a s t a r d").hit()).isTrue();
    }

    @Test
    @DisplayName("ignores punctuation and symbols inserted between characters")
    void separatorVariant() {
        assertThat(filter.check("蠢-货").hit()).isTrue();
        assertThat(filter.check("b.a.s.t.a.r.d").hit()).isTrue();
    }

    @Test
    @DisplayName("returns not-hit for null, empty, blank and separator-only input")
    void blankInputsDoNotHit() {
        assertThat(filter.check(null).hit()).isFalse();
        assertThat(filter.check("").hit()).isFalse();
        assertThat(filter.check("   ").hit()).isFalse();
        assertThat(filter.check("!!!???---").hit()).isFalse();
        assertThat(filter.check(null).category()).isNull();
    }

    @Test
    @DisplayName("does not match normal English words (false-positive guards)")
    void falsePositiveGuards() {
        for (String word : new String[] {"class", "assignment", "classic", "passenger"}) {
            assertThat(filter.check(word).hit()).as(word).isFalse();
        }
    }

    @Test
    @DisplayName("does not match legitimate Chinese nicknames")
    void legitimateNickname() {
        assertThat(filter.check("E2E_小明").hit()).isFalse();
    }
}
