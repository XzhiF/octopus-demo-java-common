package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NameRuleTest {

    private final NameRule rule = new NameRule();

    @Test
    @DisplayName("type is NAME")
    void type_isName() {
        assertThat(rule.type()).isEqualTo(MaskType.NAME);
    }

    @Test
    @DisplayName("收件人：张三 masks only the name")
    void find_receiverName_masksName() {
        assertThat(rule.find("收件人：张三"))
                .isEqualTo(List.of(new MaskHit(4, 6, "张*")));
    }

    @Test
    @DisplayName("姓名:欧阳娜娜 keeps first char, stars the rest")
    void find_fourCharName_masksRest() {
        assertThat(rule.find("姓名:欧阳娜娜"))
                .isEqualTo(List.of(new MaskHit(3, 7, "欧***")));
    }

    @Test
    @DisplayName("pause after name is not swallowed")
    void find_nameFollowedByPunctuation_stopsAtName() {
        String text = "联系人：李四，电话13812345678";
        assertThat(rule.find(text))
                .isEqualTo(List.of(new MaskHit(4, 6, "李*")));
    }

    @Test
    @DisplayName("unlabeled name is not touched")
    void find_nameWithoutLabel_noHit() {
        assertThat(rule.find("张三今天没来")).isEmpty();
    }

    @Test
    @DisplayName("space as separator still masks the name")
    void find_spaceSeparator_masksName() {
        assertThat(rule.find("姓名 张三"))
                .isEqualTo(List.of(new MaskHit(3, 5, "张*")));
    }

    @Test
    @DisplayName("no separator at all still masks the name")
    void find_noSeparator_masksName() {
        assertThat(rule.find("联系人张三"))
                .isEqualTo(List.of(new MaskHit(3, 5, "张*")));
    }

    @Test
    @DisplayName("field label following the anchor is not mistaken for a name")
    void find_followingFieldLabel_noHit() {
        assertThat(rule.find("收货人地址：北京市朝阳区")).isEmpty();
        assertThat(rule.find("联系人名单")).isEmpty();
    }

    @Test
    @DisplayName("label with empty value is not a hit")
    void find_emptyName_noHit() {
        assertThat(rule.find("姓名：")).isEmpty();
    }

    @Test
    @DisplayName("null and empty return empty list")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
