package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boundary tests for {@link PhoneUtils} (ticket 02).
 * Expected values come from spec US1-US3 / KD1-KD2, not from the implementation.
 */
class PhoneUtilsTest {

    // ---------- isMobile ----------

    @Test
    @DisplayName("isMobile returns true for valid 11-digit mainland mobile number")
    void isMobile_validNumber_returnsTrue() {
        assertThat(PhoneUtils.isMobile("13812345678")).isTrue();
    }

    @Test
    @DisplayName("isMobile accepts second digit 9 (upper boundary of 3-9)")
    void isMobile_secondDigitNine_returnsTrue() {
        assertThat(PhoneUtils.isMobile("19912345678")).isTrue();
    }

    @Test
    @DisplayName("isMobile returns false for 10 digits")
    void isMobile_tenDigits_returnsFalse() {
        assertThat(PhoneUtils.isMobile("1381234567")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false for 12 digits")
    void isMobile_twelveDigits_returnsFalse() {
        assertThat(PhoneUtils.isMobile("138123456789")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false when a character is not a digit")
    void isMobile_nonDigit_returnsFalse() {
        assertThat(PhoneUtils.isMobile("1381234567a")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false for second digit 2 (below 3-9 range)")
    void isMobile_secondDigitTwo_returnsFalse() {
        assertThat(PhoneUtils.isMobile("12812345678")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false for second digit 0 (below 3-9 range)")
    void isMobile_secondDigitZero_returnsFalse() {
        assertThat(PhoneUtils.isMobile("10812345678")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false when first digit is not 1")
    void isMobile_firstDigitNotOne_returnsFalse() {
        assertThat(PhoneUtils.isMobile("23812345678")).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false for null")
    void isMobile_null_returnsFalse() {
        assertThat(PhoneUtils.isMobile(null)).isFalse();
    }

    @Test
    @DisplayName("isMobile returns false for empty string")
    void isMobile_empty_returnsFalse() {
        assertThat(PhoneUtils.isMobile("")).isFalse();
    }

    // ---------- mask ----------

    @Test
    @DisplayName("mask produces 3-4-4 starred form for valid mobile number")
    void mask_validNumber_returns344Mask() {
        assertThat(PhoneUtils.mask("13812345678")).isEqualTo("138****5678");
    }

    @Test
    @DisplayName("mask returns empty string for null")
    void mask_null_returnsEmpty() {
        assertThat(PhoneUtils.mask(null)).isEmpty();
    }

    @Test
    @DisplayName("mask of 7-digit short number returns seven stars")
    void mask_sevenDigitShortNumber_returnsSevenStars() {
        assertThat(PhoneUtils.mask("1381234")).isEqualTo("*******");
    }

    @Test
    @DisplayName("mask of non-digit string returns same-length stars")
    void mask_nonDigitString_returnsSameLengthStars() {
        assertThat(PhoneUtils.mask("12ab34")).isEqualTo("******");
    }

    @Test
    @DisplayName("mask of invalid 11-digit number returns eleven stars")
    void mask_invalidElevenDigits_returnsElevenStars() {
        assertThat(PhoneUtils.mask("12812345678")).isEqualTo("***********");
    }

    @Test
    @DisplayName("mask of empty string returns empty string")
    void mask_empty_returnsEmpty() {
        assertThat(PhoneUtils.mask("")).isEmpty();
    }
}
