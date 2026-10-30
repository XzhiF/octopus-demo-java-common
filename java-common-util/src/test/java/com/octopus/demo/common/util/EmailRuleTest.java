package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmailRuleTest {

    private final EmailRule rule = new EmailRule();

    @Test
    @DisplayName("type is EMAIL")
    void type_isEmail() {
        assertThat(rule.type()).isEqualTo(MaskType.EMAIL);
    }

    @Test
    @DisplayName("plain email is masked keeping local head and tail")
    void find_plainEmail_masked() {
        assertThat(rule.find("alice@example.com"))
                .isEqualTo(List.of(new MaskHit(0, 17, "a***e@example.com")));
    }

    @Test
    @DisplayName("local part of length <= 2 is replaced entirely")
    void find_shortLocal_maskedEntirely() {
        assertThat(rule.find("ab@x.cn"))
                .isEqualTo(List.of(new MaskHit(0, 7, "***@x.cn")));
    }

    @Test
    @DisplayName("email embedded in text is located precisely")
    void find_embeddedEmail_hitRangeExact() {
        String text = "联系 alice@example.com 谢谢";
        assertThat(rule.find(text))
                .isEqualTo(List.of(new MaskHit(3, 20, "a***e@example.com")));
    }

    @Test
    @DisplayName("string without @ is not a hit")
    void find_notAnEmail_noHit() {
        assertThat(rule.find("not-an-email")).isEmpty();
    }

    @Test
    @DisplayName("domain without a dot is not a hit")
    void find_domainWithoutDot_noHit() {
        assertThat(rule.find("alice@localhost")).isEmpty();
    }

    @Test
    @DisplayName("null and empty return empty list")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
