package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ticket 03：自定义词库 append/override 入口 + 合并语义（US7-9，KD8/9/10）。
 */
class CustomLexiconTest {

    // US7：append = 默认 ∪ 自定义（并集生效，显式类别）
    @Test
    void appendWords_unionOfDefaultAndCustom() {
        SensitiveWordFilter filter = SensitiveWordFilter.builder()
                .appendWords(SensitiveWordCategory.INSULT, List.of("测试违禁词"))
                .build();
        List<SensitiveWordHit> hits = filter.detect("这是测试违禁词");
        assertThat(hits).hasSize(1);
        assertThat(hits.get(0).word()).isEqualTo("测试违禁词");
        assertThat(hits.get(0).category()).isEqualTo(SensitiveWordCategory.INSULT);
        assertThat(hits.get(0).startIndex()).isEqualTo(2);
        assertThat(hits.get(0).endIndex()).isEqualTo(6);
        // 默认词仍能命中
        assertThat(filter.detect("你个傻逼")).hasSize(1);
    }

    // US8：override = 仅自定义，默认词不再命中
    @Test
    void overrideWords_replacesDefaultEntirely() {
        SensitiveWordFilter filter = SensitiveWordFilter.builder()
                .overrideWords(List.of(new WordEntry("独家词", SensitiveWordCategory.PORNO)))
                .build();
        assertThat(filter.detect("独家词")).hasSize(1);
        assertThat(filter.detect("独家词").get(0).category()).isEqualTo(SensitiveWordCategory.PORNO);
        assertThat(filter.detect("傻逼")).isEmpty();
        // 对比默认实例：傻逼在原实例是命中的
        assertThat(SensitiveWordFilter.builder().build().detect("傻逼")).isNotEmpty();
    }

    // US9：重复追加不产两条；跨类别冲突自定义覆盖默认
    @Test
    void appendWords_dedupAndCategoryOverride() {
        SensitiveWordFilter dup = SensitiveWordFilter.builder()
                .appendWords(SensitiveWordCategory.INSULT, List.of("傻逼", "傻逼"))
                .build();
        assertThat(dup.detect("傻逼")).hasSize(1);

        SensitiveWordFilter conflict = SensitiveWordFilter.builder()
                .appendWords(SensitiveWordCategory.PORNO, List.of("傻逼"))
                .build();
        List<SensitiveWordHit> hits = conflict.detect("傻逼");
        assertThat(hits).hasSize(1);
        assertThat(hits.get(0).category()).isEqualTo(SensitiveWordCategory.PORNO);
    }

    // 空集合：append 空 ≡ 默认行为，override 空 ≡ 空词库，不抛异常
    @Test
    void emptyCollections_deterministic() {
        SensitiveWordFilter appendEmpty = SensitiveWordFilter.builder()
                .appendWords(SensitiveWordCategory.INSULT, List.of())
                .build();
        assertThat(appendEmpty.detect("傻逼")).hasSize(1);
        assertThat(appendEmpty.detect("测试违禁词")).isEmpty();

        SensitiveWordFilter overrideEmpty = SensitiveWordFilter.builder()
                .overrideWords(List.of())
                .build();
        assertThat(overrideEmpty.detect("傻逼")).isEmpty();
        assertThat(overrideEmpty.mask("傻逼")).isEqualTo("傻逼");
    }
}
