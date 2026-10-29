package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ticket 01：默认词库 + Trie 字面匹配 + detect/mask 基本行为。
 */
class SensitiveWordFilterTest {

    private final SensitiveWordFilter filter = SensitiveWordFilter.builder().build();

    @Test
    void detect_singleHit_wordCategoryAndIndices() {
        List<SensitiveWordHit> hits = filter.detect("你个傻逼");
        assertThat(hits).hasSize(1);
        SensitiveWordHit hit = hits.get(0);
        assertThat(hit.word()).isEqualTo("傻逼");
        assertThat(hit.category()).isEqualTo(SensitiveWordCategory.INSULT);
        assertThat(hit.startIndex()).isEqualTo(2);
        assertThat(hit.endIndex()).isEqualTo(3);
    }

    @Test
    void detect_multiWordMultiCategory_allHitNotMerged() {
        List<SensitiveWordHit> hits = filter.detect("傻逼看色情");
        assertThat(hits).hasSize(2);
        assertThat(hits).extracting(SensitiveWordHit::category)
                .containsExactly(SensitiveWordCategory.INSULT, SensitiveWordCategory.PORNO);
        assertThat(hits.get(0).startIndex()).isZero();
        assertThat(hits.get(0).endIndex()).isEqualTo(1);
        assertThat(hits.get(1).word()).isEqualTo("色情");
        assertThat(hits.get(1).startIndex()).isEqualTo(3);
        assertThat(hits.get(1).endIndex()).isEqualTo(4);
    }

    @Test
    void mask_hitsStarredLengthPreserved() {
        String text = "傻逼看色情";
        String masked = filter.mask(text);
        assertThat(masked).isEqualTo("**看**");
        assertThat(masked).hasSize(text.length());
        // ponytail: mask 用 detect 结果回写，长度不变是契约
    }

    @Test
    void cleanText_noHitMaskUnchanged() {
        assertThat(filter.detect("今天天气不错")).isEmpty();
        assertThat(filter.mask("今天天气不错")).isEqualTo("今天天气不错");
    }

    @Test
    void nullAndEmpty_deterministicEmpty() {
        assertThat(filter.detect(null)).isEmpty();
        assertThat(filter.detect("")).isEmpty();
        assertThat(filter.mask(null)).isNull();
        assertThat(filter.mask("")).isEmpty();
    }

    @Test
    void lexicon_hasAtLeast30PerCategory() {
        Map<String, SensitiveWordCategory> lex;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("sensitive-words.txt")) {
            assertThat(in).isNotNull();
            lex = SensitiveWordFilter.loadLexicon(in);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertThat(lex.values().stream().filter(c -> c == SensitiveWordCategory.INSULT).count())
                .isGreaterThanOrEqualTo(30);
        assertThat(lex.values().stream().filter(c -> c == SensitiveWordCategory.PORNO).count())
                .isGreaterThanOrEqualTo(30);
    }

    @Test
    void loadLexicon_illegalLinesSkippedNotFatal() {
        Map<String, SensitiveWordCategory> lex;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("sensitive-words-bad.txt")) {
            assertThat(in).isNotNull();
            lex = SensitiveWordFilter.loadLexicon(in);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        // 非法行（无冒号/未知类别/空词/空类别/注释）全部跳过，合法行照常
        assertThat(lex).containsEntry("傻逼", SensitiveWordCategory.INSULT);
        assertThat(lex).containsEntry("色情", SensitiveWordCategory.PORNO);
        assertThat(lex).hasSize(2);
    }
}
