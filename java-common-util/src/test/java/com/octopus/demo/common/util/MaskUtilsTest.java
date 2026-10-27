package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MaskUtilsTest {

    @Test
    @DisplayName("maskPhone keeps first 3 and last 4 digits")
    void maskPhone_validNumber() {
        assertThat(MaskUtils.maskPhone("13812345678")).isEqualTo("138****5678");
    }

    @Test
    @DisplayName("maskEmail masks local part, keeps domain")
    void maskEmail_validEmail() {
        assertThat(MaskUtils.maskEmail("alice@example.com")).isEqualTo("a***e@example.com");
    }

    @Test
    @DisplayName("maskPhone returns null as-is")
    void maskPhone_null_returnsNull() {
        assertThat(MaskUtils.maskPhone(null)).isNull();
    }

    @Test
    @DisplayName("maskPhone returns empty string as-is")
    void maskPhone_empty_returnsAsIs() {
        assertThat(MaskUtils.maskPhone("")).isEmpty();
    }

    @Test
    @DisplayName("maskPhone returns short number as-is")
    void maskPhone_wrongLength_returnsAsIs() {
        assertThat(MaskUtils.maskPhone("1381234")).isEqualTo("1381234");
    }

    @Test
    @DisplayName("maskPhone returns 11-char non-digit input as-is")
    void maskPhone_nonDigit_returnsAsIs() {
        assertThat(MaskUtils.maskPhone("138abcd5678")).isEqualTo("138abcd5678");
    }

    @Test
    @DisplayName("maskEmail returns input without @ as-is")
    void maskEmail_noAtSign_returnsAsIs() {
        assertThat(MaskUtils.maskEmail("alice-at-example")).isEqualTo("alice-at-example");
    }

    @Test
    @DisplayName("maskEmail replaces whole local part when length <= 2")
    void maskEmail_shortLocal_replacedEntirely() {
        assertThat(MaskUtils.maskEmail("ab@example.com")).isEqualTo("***@example.com");
        assertThat(MaskUtils.maskEmail("a@example.com")).isEqualTo("***@example.com");
    }

    @Test
    @DisplayName("maskEmail returns null and empty as-is")
    void maskEmail_nullOrEmpty_returnsAsIs() {
        assertThat(MaskUtils.maskEmail(null)).isNull();
        assertThat(MaskUtils.maskEmail("")).isEmpty();
    }
}
