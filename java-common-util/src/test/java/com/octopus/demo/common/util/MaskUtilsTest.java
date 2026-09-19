package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MaskUtilsTest {

    @Test
    @DisplayName("maskPhone keeps first 3 and last 4 digits")
    void maskPhone_valid() {
        assertThat(MaskUtils.maskPhone("13812345678")).isEqualTo("138****5678");
    }

    @Test
    @DisplayName("maskPhone returns null as-is")
    void maskPhone_null() {
        assertThat(MaskUtils.maskPhone(null)).isNull();
    }

    @Test
    @DisplayName("maskPhone returns empty string as-is")
    void maskPhone_empty() {
        assertThat(MaskUtils.maskPhone("")).isEmpty();
    }

    @Test
    @DisplayName("maskPhone returns too-short input as-is")
    void maskPhone_tooShort() {
        assertThat(MaskUtils.maskPhone("1381234567")).isEqualTo("1381234567");
    }

    @Test
    @DisplayName("maskIdCard keeps first 6 and last 4 digits")
    void maskIdCard_valid() {
        assertThat(MaskUtils.maskIdCard("110101199003078888")).isEqualTo("110101********8888");
    }

    @Test
    @DisplayName("maskIdCard returns null as-is")
    void maskIdCard_null() {
        assertThat(MaskUtils.maskIdCard(null)).isNull();
    }

    @Test
    @DisplayName("maskIdCard returns empty string as-is")
    void maskIdCard_empty() {
        assertThat(MaskUtils.maskIdCard("")).isEmpty();
    }

    @Test
    @DisplayName("maskIdCard returns too-short input as-is")
    void maskIdCard_tooShort() {
        assertThat(MaskUtils.maskIdCard("11010119900307888")).isEqualTo("11010119900307888");
    }
}
