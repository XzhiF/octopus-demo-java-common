package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MaskUtilsTest {

    // ---------- maskPhone ----------

    @Test
    @DisplayName("maskPhone masks a valid mainland mobile keeping first 3 and last 4")
    void maskPhone_valid() {
        assertThat(MaskUtils.maskPhone("13812348000")).isEqualTo("138****8000");
    }

    @Test
    @DisplayName("maskPhone returns null for null input")
    void maskPhone_null() {
        assertThat(MaskUtils.maskPhone(null)).isNull();
    }

    @Test
    @DisplayName("maskPhone returns empty string unchanged")
    void maskPhone_empty() {
        assertThat(MaskUtils.maskPhone("")).isEmpty();
    }

    @Test
    @DisplayName("maskPhone returns all-whitespace unchanged")
    void maskPhone_blank() {
        assertThat(MaskUtils.maskPhone("   ")).isEqualTo("   ");
    }

    @Test
    @DisplayName("maskPhone falls back to first char + *** for wrong length")
    void maskPhone_wrongLength() {
        assertThat(MaskUtils.maskPhone("123")).isEqualTo("1***");
        assertThat(MaskUtils.maskPhone("138123480001")).isEqualTo("1***");
        assertThat(MaskUtils.maskPhone("1381234800")).isEqualTo("1***");
    }

    @Test
    @DisplayName("maskPhone falls back for non-digit or not starting with 1")
    void maskPhone_invalidFormat() {
        assertThat(MaskUtils.maskPhone("23812348000")).isEqualTo("2***");
        assertThat(MaskUtils.maskPhone("1381234800a")).isEqualTo("1***");
    }

    @Test
    @DisplayName("maskPhone returns single star for single code point")
    void maskPhone_singleCodePoint() {
        assertThat(MaskUtils.maskPhone("1")).isEqualTo("*");
        assertThat(MaskUtils.maskPhone("a")).isEqualTo("*");
    }

    @Test
    @DisplayName("maskPhone never returns the original for long invalid input")
    void maskPhone_longInvalid_neverVerbatim() {
        String input = "abcdefghijklmnop";
        assertThat(MaskUtils.maskPhone(input)).isNotEqualTo(input);
        assertThat(MaskUtils.maskPhone(input)).isEqualTo("a***");
    }

    // ---------- maskEmail ----------

    @Test
    @DisplayName("maskEmail keeps first local char, fixed ***, and domain verbatim")
    void maskEmail_valid() {
        assertThat(MaskUtils.maskEmail("zhangsan@example.com")).isEqualTo("z***@example.com");
    }

    @Test
    @DisplayName("maskEmail returns ***@domain when local part is a single char")
    void maskEmail_singleCharLocal() {
        assertThat(MaskUtils.maskEmail("a@example.com")).isEqualTo("***@example.com");
    }

    @Test
    @DisplayName("maskEmail returns null for null input")
    void maskEmail_null() {
        assertThat(MaskUtils.maskEmail(null)).isNull();
    }

    @Test
    @DisplayName("maskEmail returns empty string unchanged")
    void maskEmail_empty() {
        assertThat(MaskUtils.maskEmail("")).isEmpty();
    }

    @Test
    @DisplayName("maskEmail returns all-whitespace unchanged")
    void maskEmail_blank() {
        assertThat(MaskUtils.maskEmail(" \t ")).isEqualTo(" \t ");
    }

    @Test
    @DisplayName("maskEmail falls back when there is no @")
    void maskEmail_noAt() {
        assertThat(MaskUtils.maskEmail("not-an-email")).isEqualTo("n***");
    }

    @Test
    @DisplayName("maskEmail falls back when local part is empty")
    void maskEmail_emptyLocal() {
        assertThat(MaskUtils.maskEmail("@example.com")).isEqualTo("@***");
    }

    @Test
    @DisplayName("maskEmail falls back when domain is empty")
    void maskEmail_emptyDomain() {
        assertThat(MaskUtils.maskEmail("a@")).isEqualTo("a***");
        assertThat(MaskUtils.maskEmail("abc@")).isEqualTo("a***");
    }

    @Test
    @DisplayName("maskEmail returns single star for single code point fallback")
    void maskEmail_singleCodePoint() {
        assertThat(MaskUtils.maskEmail("@")).isEqualTo("*");
        assertThat(MaskUtils.maskEmail("x")).isEqualTo("*");
    }

    @Test
    @DisplayName("maskEmail never returns the original for invalid input")
    void maskEmail_invalid_neverVerbatim() {
        String input = "plain-text-value";
        assertThat(MaskUtils.maskEmail(input)).isNotEqualTo(input);
    }

    // ---------- maskNickname ----------

    @Test
    @DisplayName("maskNickname keeps first char + single star for multi-char input")
    void maskNickname_multiChar() {
        assertThat(MaskUtils.maskNickname("张三")).isEqualTo("张*");
        assertThat(MaskUtils.maskNickname("Tom")).isEqualTo("T*");
    }

    @Test
    @DisplayName("maskNickname returns single star for single char")
    void maskNickname_singleChar() {
        assertThat(MaskUtils.maskNickname("张")).isEqualTo("*");
        assertThat(MaskUtils.maskNickname("T")).isEqualTo("*");
    }

    @Test
    @DisplayName("maskNickname returns null for null input")
    void maskNickname_null() {
        assertThat(MaskUtils.maskNickname(null)).isNull();
    }

    @Test
    @DisplayName("maskNickname returns empty string unchanged")
    void maskNickname_empty() {
        assertThat(MaskUtils.maskNickname("")).isEmpty();
    }

    @Test
    @DisplayName("maskNickname returns all-whitespace unchanged")
    void maskNickname_blank() {
        assertThat(MaskUtils.maskNickname("  ")).isEqualTo("  ");
    }

    @Test
    @DisplayName("maskNickname does not split surrogate pairs for emoji")
    void maskNickname_emoji_notSplit() {
        String emoji = "😀"; // 😀 single code point, two chars
        assertThat(MaskUtils.maskNickname(emoji)).isEqualTo("*");

        String emojiNickname = "😀Tom";
        assertThat(MaskUtils.maskNickname(emojiNickname)).isEqualTo("😀*");
    }

    @Test
    @DisplayName("maskNickname star count is fixed regardless of input length")
    void maskNickname_fixedStarCount() {
        assertThat(MaskUtils.maskNickname("ab")).isEqualTo("a*");
        assertThat(MaskUtils.maskNickname("abcdefghijklmnop")).isEqualTo("a*");
    }

    @Test
    @DisplayName("maskPhone star count is fixed regardless of length")
    void maskPhone_fixedStarCount() {
        assertThat(MaskUtils.maskPhone("abc")).isEqualTo("a***");
        assertThat(MaskUtils.maskPhone("abcdefghijklmnop")).isEqualTo("a***");
    }

    @Test
    @DisplayName("maskEmail star count is fixed regardless of local length")
    void maskEmail_fixedStarCount() {
        assertThat(MaskUtils.maskEmail("ab@x.com")).isEqualTo("a***@x.com");
        assertThat(MaskUtils.maskEmail("abcdefghijklmnop@x.com")).isEqualTo("a***@x.com");
    }
}
