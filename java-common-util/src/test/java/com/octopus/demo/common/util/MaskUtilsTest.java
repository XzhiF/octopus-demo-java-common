package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MaskUtilsTest {

    // ---------- maskPhone ----------

    @Test
    @DisplayName("maskPhone keeps first 3 and last 4 digits of an 11-digit number")
    void maskPhone_normalSample_keepsOuterDigits() {
        assertThat(MaskUtils.maskPhone("13812345678")).isEqualTo("138****5678");
    }

    @Test
    @DisplayName("maskPhone of value shorter than 11 digits is fully masked, same length")
    void maskPhone_tooShort_fullyMasked() {
        assertThat(MaskUtils.maskPhone("138")).isEqualTo("***");
    }

    @Test
    @DisplayName("maskPhone of value longer than 11 digits is fully masked, same length")
    void maskPhone_tooLong_fullyMasked() {
        assertThat(MaskUtils.maskPhone("138123456789")).isEqualTo("************");
    }

    @Test
    @DisplayName("maskPhone returns null and empty input unchanged, never throws")
    void maskPhone_nullOrEmpty_returnedAsIs() {
        assertThat(MaskUtils.maskPhone(null)).isNull();
        assertThat(MaskUtils.maskPhone("")).isEmpty();
        assertThat(MaskUtils.maskPhone("   ")).isEqualTo("   ");
    }

    // ---------- maskIdCard ----------

    @Test
    @DisplayName("maskIdCard keeps first 3 and last 2 characters of an 18-char number")
    void maskIdCard_normalSample_keepsOuterCharacters() {
        assertThat(MaskUtils.maskIdCard("110101199003078888"))
                .isEqualTo("110" + "*".repeat(13) + "88")
                .hasSize(18);
    }

    @Test
    @DisplayName("maskIdCard at the exact minimum length of 8 keeps first 3 and last 2")
    void maskIdCard_exactMinLength_keepsOuterCharacters() {
        assertThat(MaskUtils.maskIdCard("12345678")).isEqualTo("123***78");
    }

    @Test
    @DisplayName("maskIdCard shorter than 8 characters is fully masked, same length")
    void maskIdCard_tooShort_fullyMasked() {
        assertThat(MaskUtils.maskIdCard("1234567")).isEqualTo("*******");
    }

    @Test
    @DisplayName("maskIdCard returns null and empty input unchanged, never throws")
    void maskIdCard_nullOrEmpty_returnedAsIs() {
        assertThat(MaskUtils.maskIdCard(null)).isNull();
        assertThat(MaskUtils.maskIdCard("")).isEmpty();
    }

    // ---------- maskEmail ----------

    @Test
    @DisplayName("maskEmail masks username and leaves the domain untouched")
    void maskEmail_normalSample_masksUsernameOnly() {
        assertThat(MaskUtils.maskEmail("someone@example.com"))
                .isEqualTo("so****e@example.com");
    }

    @Test
    @DisplayName("maskEmail with 2-char username keeps both outer characters")
    void maskEmail_twoCharUsername_keepsOuterCharacters() {
        assertThat(MaskUtils.maskEmail("ab@x.com")).isEqualTo("a****b@x.com");
    }

    @Test
    @DisplayName("maskEmail with username shorter than 2 chars is fully masked")
    void maskEmail_shortUsername_fullyMasked() {
        assertThat(MaskUtils.maskEmail("a@x.com")).isEqualTo("*@x.com");
    }

    @Test
    @DisplayName("maskEmail without @ applies the username rule to the whole value")
    void maskEmail_noAtSign_masksWholeValueAsUsername() {
        assertThat(MaskUtils.maskEmail("someone")).isEqualTo("so****e");
        assertThat(MaskUtils.maskEmail("a")).isEqualTo("*");
    }

    @Test
    @DisplayName("maskEmail returns null and empty input unchanged, never throws")
    void maskEmail_nullOrEmpty_returnedAsIs() {
        assertThat(MaskUtils.maskEmail(null)).isNull();
        assertThat(MaskUtils.maskEmail("")).isEmpty();
        assertThat(MaskUtils.maskEmail("  ")).isEqualTo("  ");
    }
}
