package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ticket 02：归一化——大小写/全半角/干扰符变体不漏杀，命中位置映射回原文。
 */
class NormalizationTest {

    private final SensitiveWordFilter filter = SensitiveWordFilter.builder().build();

    /** 变体数据表：text → 期望命中（归一词/类别/原文起止含端点）。新增变体只加数据不加方法。 */
    private record Variant(String text, String word, SensitiveWordCategory category, int start, int end) {
    }

    private static final List<Variant> VARIANTS = List.of(
            // 符号插
            new Variant("傻★逼", "傻逼", SensitiveWordCategory.INSULT, 0, 2),
            new Variant("傻-逼", "傻逼", SensitiveWordCategory.INSULT, 0, 2),
            // 空格插（含全角空格）
            new Variant("傻 逼", "傻逼", SensitiveWordCategory.INSULT, 0, 2),
            new Variant("傻　逼", "傻逼", SensitiveWordCategory.INSULT, 0, 2),
            // 全角
            new Variant("ＦＵＣＫ", "fuck", SensitiveWordCategory.INSULT, 0, 3),
            // 大小写混
            new Variant("Fuck", "fuck", SensitiveWordCategory.INSULT, 0, 3),
            new Variant("fUcK", "fuck", SensitiveWordCategory.INSULT, 0, 3),
            new Variant("fuck", "fuck", SensitiveWordCategory.INSULT, 0, 3),
            // 全半角混
            new Variant("FＵＣk", "fuck", SensitiveWordCategory.INSULT, 0, 3),
            // 干扰符 + 上下文混排，位置回原文
            new Variant("你个傻。逼！", "傻逼", SensitiveWordCategory.INSULT, 2, 4),
            // 汉字字母数字不跳（KD4）：a1 中间无干扰符可剥
            new Variant("傻逼a1", "傻逼", SensitiveWordCategory.INSULT, 0, 1));

    @Test
    void variants_hitWithNormalizedWordAndOriginalSpan() {
        for (Variant v : VARIANTS) {
            List<SensitiveWordHit> hits = filter.detect(v.text());
            assertThat(hits).as("text=%s", v.text()).hasSize(1);
            SensitiveWordHit hit = hits.get(0);
            assertThat(hit.word()).as("text=%s", v.text()).isEqualTo(v.word());
            assertThat(hit.category()).as("text=%s", v.text()).isEqualTo(v.category());
            assertThat(hit.startIndex()).as("text=%s", v.text()).isEqualTo(v.start());
            assertThat(hit.endIndex()).as("text=%s", v.text()).isEqualTo(v.end());
        }
    }

    @Test
    void mask_coversWholeVariantSegmentIncludingInterferenceChars() {
        assertThat(filter.mask("傻★逼")).isEqualTo("***");
        assertThat(filter.mask("你个傻。逼！")).isEqualTo("你个***！");
        assertThat(filter.mask("ＦＵＣＫ")).isEqualTo("****");
    }

    @Test
    void cleanMixedText_noFalsePositive() {
        assertThat(filter.detect("你好a1")).isEmpty();
        assertThat(filter.detect("这里的风景真美，阳光明媚！微风轻轻。吹过湖面？")).isEmpty();
    }
}
