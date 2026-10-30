package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BankCardRuleTest {

    private final BankCardRule rule = new BankCardRule();

    @Test
    @DisplayName("masks a 19-digit bank card keeping the last 4")
    void find_nineteenDigits_masked() {
        assertThat(rule.find("6222021234567890123"))
                .isEqualTo(List.of(new MaskHit(0, 19, "***************0123")));
    }

    @Test
    @DisplayName("13 digits is the shortest accepted bank card")
    void find_thirteenDigits_masked() {
        assertThat(rule.find("1234567890123"))
                .isEqualTo(List.of(new MaskHit(0, 13, "*********0123")));
    }

    @Test
    @DisplayName("declares BANK_CARD type")
    void type_isBankCard() {
        assertThat(rule.type()).isEqualTo(MaskType.BANK_CARD);
    }

    @Test
    @DisplayName("12 digits is too short to be a bank card")
    void find_twelveDigits_notMatched() {
        assertThat(rule.find("123456789012")).isEmpty();
    }

    @Test
    @DisplayName("20 digits exceeds the bank card length range")
    void find_twentyDigits_notMatched() {
        assertThat(rule.find("12345678901234567890")).isEmpty();
    }

    @Test
    @DisplayName("a valid id card range is not claimed by the bank card rule")
    void find_validIdCard_notClaimed() {
        assertThat(rule.find("11010519491231002X")).isEmpty();
    }

    @Test
    @DisplayName("an 18-digit number failing the id checksum falls back to bank card")
    void find_badChecksum18Digits_fallsBackToBankCard() {
        assertThat(rule.find("110105194912310021"))
                .isEqualTo(List.of(new MaskHit(0, 18, "**************0021")));
    }

    @Test
    @DisplayName("null and empty input yield no hits")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
