package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IdCardRuleTest {

    private final IdCardRule rule = new IdCardRule();

    @Test
    @DisplayName("masks a valid 18-digit id card")
    void find_validIdCard_masked() {
        assertThat(rule.find("11010519491231002X"))
                .isEqualTo(List.of(new MaskHit(0, 18, "110105********002X")));
    }

    @Test
    @DisplayName("lowercase x is accepted and preserved")
    void find_lowercaseX_masked() {
        assertThat(rule.find("11010519491231002x"))
                .isEqualTo(List.of(new MaskHit(0, 18, "110105********002x")));
    }

    @Test
    @DisplayName("declares ID_CARD type")
    void type_isIdCard() {
        assertThat(rule.type()).isEqualTo(MaskType.ID_CARD);
    }

    @Test
    @DisplayName("id card embedded in text keeps surrounding characters")
    void find_embeddedIdCard_rangeIsCorrect() {
        assertThat(rule.find("身份证11010519491231002X手机"))
                .isEqualTo(List.of(new MaskHit(3, 21, "110105********002X")));
    }

    @Test
    @DisplayName("wrong checksum 110105194912310021 is not matched")
    void find_badChecksum_notMatched() {
        assertThat(rule.find("110105194912310021")).isEmpty();
    }

    @Test
    @DisplayName("null and empty input yield no hits")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
