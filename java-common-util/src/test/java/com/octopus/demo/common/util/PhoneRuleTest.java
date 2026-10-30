package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneRuleTest {

    private final PhoneRule rule = new PhoneRule();

    @Test
    @DisplayName("masks a phone embedded in text")
    void find_embeddedPhone_masked() {
        assertThat(rule.find("联系 13812345678 谢谢"))
                .isEqualTo(java.util.List.of(new MaskHit(3, 14, "138****5678")));
    }

    @Test
    @DisplayName("declares PHONE type")
    void type_isPhone() {
        assertThat(rule.type()).isEqualTo(MaskType.PHONE);
    }

    @Test
    @DisplayName("invalid segment 12345678901 is not matched")
    void find_invalidSegment_notMatched() {
        assertThat(rule.find("12345678901")).isEmpty();
    }

    @Test
    @DisplayName("10-digit number is not matched")
    void find_tenDigits_notMatched() {
        assertThat(rule.find("1381234567")).isEmpty();
    }

    @Test
    @DisplayName("digit immediately before the candidate prevents a match")
    void find_precededByDigit_notMatched() {
        assertThat(rule.find("01381234567")).isEmpty();
    }

    @Test
    @DisplayName("null and empty input yield no hits")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
