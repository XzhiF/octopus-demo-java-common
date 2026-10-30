package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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

    @Test
    @DisplayName("maskText single-type examples match the documented outputs")
    void maskText_documentedExamples_match() {
        assertThat(MaskUtils.maskText("联系 13812345678")).isEqualTo("联系 138****5678");
        assertThat(MaskUtils.maskText("alice@example.com")).isEqualTo("a***e@example.com");
        assertThat(MaskUtils.maskText("11010519491231002X")).isEqualTo("110105********002X");
        assertThat(MaskUtils.maskText("6222021234567890123")).isEqualTo("***************0123");
        assertThat(MaskUtils.maskText("收件人：张三")).isEqualTo("收件人：张*");
        assertThat(MaskUtils.maskText("地址：北京市朝阳区建国路88号")).isEqualTo("地址：北京市朝阳区****");
    }

    @Test
    @DisplayName("maskText masks all six types in one mixed line and keeps other characters")
    void maskText_mixedText_allTypesMasked() {
        String text = "用户 13812345678 邮箱 alice@example.com 身份证 11010519491231002X "
                + "银行卡 6222021234567890123 收件人：张三 地址：北京市朝阳区建国路88号";
        assertThat(MaskUtils.maskText(text)).isEqualTo(
                "用户 138****5678 邮箱 a***e@example.com 身份证 110105********002X "
                        + "银行卡 ***************0123 收件人：张* 地址：北京市朝阳区****");
    }

    @Test
    @DisplayName("maskText with a type subset leaves other types untouched")
    void maskText_selectiveTypes_onlySelectedMasked() {
        String text = "用户 13812345678 邮箱 alice@example.com 身份证 11010519491231002X "
                + "银行卡 6222021234567890123 收件人：张三 地址：北京市朝阳区建国路88号";
        assertThat(MaskUtils.maskText(text, Set.of(MaskType.PHONE, MaskType.ID_CARD))).isEqualTo(
                "用户 138****5678 邮箱 alice@example.com 身份证 110105********002X "
                        + "银行卡 6222021234567890123 收件人：张三 地址：北京市朝阳区建国路88号");
    }

    @Test
    @DisplayName("maskText returns null, empty, plain text and over-long digit run as-is")
    void maskText_nullAndEdgeInputs_returnedAsIs() {
        assertThat(MaskUtils.maskText(null)).isNull();
        assertThat(MaskUtils.maskText("")).isEmpty();
        assertThat(MaskUtils.maskText("今天没有敏感信息")).isEqualTo("今天没有敏感信息");
        assertThat(MaskUtils.maskText("12345678901234567890")).isEqualTo("12345678901234567890");
    }

    @Test
    @DisplayName("maskText is idempotent")
    void maskText_isIdempotent() {
        String text = "用户 13812345678 邮箱 alice@example.com 身份证 11010519491231002X";
        String once = MaskUtils.maskText(text);
        assertThat(MaskUtils.maskText(once)).isEqualTo(once);
    }

    @Test
    @DisplayName("an 11-digit run inside an 18-digit id card is not masked twice")
    void maskText_phoneInsideIdCard_notDoubleMasked() {
        String text = "证件 110105138123456783 结束";
        String masked = MaskUtils.maskText(text);
        assertThat(masked).isEqualTo("证件 110105********6783 结束");
        assertThat(masked).doesNotContain("138****");
    }

    @Test
    @DisplayName("an unlabeled name is deliberately left untouched")
    void maskText_nameWithoutLabel_untouched() {
        assertThat(MaskUtils.maskText("张三今天没来")).isEqualTo("张三今天没来");
    }

    @Test
    @DisplayName("a null or empty type set means all types are enabled")
    void maskText_nullOrEmptyTypeSet_meansAll() {
        String text = "联系 13812345678";
        assertThat(MaskUtils.maskText(text, null)).isEqualTo("联系 138****5678");
        assertThat(MaskUtils.maskText(text, Set.of())).isEqualTo("联系 138****5678");
    }

    @Test
    @DisplayName("a rule throwing RuntimeException is skipped while other rules still apply")
    void maskText_ruleThrows_otherRulesStillApply() {
        MaskRule exploding = new MaskRule() {
            @Override
            public MaskType type() {
                return MaskType.EMAIL;
            }

            @Override
            public List<MaskHit> find(String text) {
                throw new IllegalStateException("boom");
            }
        };
        List<MaskRule> rules = List.of(exploding, new PhoneRule());
        assertThat(MaskUtils.maskText("联系 13812345678", null, rules)).isEqualTo("联系 138****5678");
    }
}
