package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ticket 04：边界健壮（US5）——null/空/超长行为确定、detect O(n) &lt; 1s、并发一致性（KD5）。
 */
class SensitiveWordEdgeCaseTest {

    private final SensitiveWordFilter filter = SensitiveWordFilter.builder().build();

    @Test
    void nullAndEmptyAreDeterministic() {
        assertThat(filter.detect(null)).isEmpty();
        assertThat(filter.detect("")).isEmpty();
        assertThat(filter.mask(null)).isNull();
        assertThat(filter.mask("")).isEmpty();
    }

    @Test
    void tenThousandCharsDetectsVariantUnderOneSecond() {
        String filler = "晴".repeat(10_000);
        String text = filler + "傻★逼" + filler; // 3 字符敏感词变体嵌进 20003 字

        long start = System.nanoTime();
        List<SensitiveWordHit> hits = filter.detect(text);
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        assertThat(hits).hasSize(1);
        assertThat(hits.getFirst().word()).isEqualTo("傻逼");
        assertThat(hits.getFirst().category()).isEqualTo(SensitiveWordCategory.INSULT);
        assertThat(hits.getFirst().startIndex()).isEqualTo(10_000);
        assertThat(hits.getFirst().endIndex()).isEqualTo(10_002);
        assertThat(elapsed).isLessThan(Duration.ofSeconds(1));
    }

    @Test
    void concurrentDetectOnSameInstanceIsConsistent() throws Exception {
        String text = "你个傻★逼 还有ＦＵＣＫ 与 智障";
        List<SensitiveWordHit> expected = filter.detect(text);
        assertThat(expected).isNotEmpty();

        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            Callable<List<SensitiveWordHit>> job = () -> filter.detect(text);
            List<Future<List<SensitiveWordHit>>> futures = pool.invokeAll(java.util.Collections.nCopies(16, job));
            for (Future<List<SensitiveWordHit>> f : futures) {
                assertThat(f.get()).isEqualTo(expected);
            }
        }
    }

    /** V2：照抄 README「敏感词过滤」示例，保证文档代码可编译、可跑。 */
    @Test
    void readmeExampleCompilesAndRuns() {
        SensitiveWordFilter filter = SensitiveWordFilter.builder().build();

        List<SensitiveWordHit> hits = filter.detect("你个傻逼");
        String masked = filter.mask("你个傻逼");
        assertThat(hits).singleElement()
                .extracting(SensitiveWordHit::word, SensitiveWordHit::category)
                .containsExactly("傻逼", SensitiveWordCategory.INSULT);
        assertThat(masked).isEqualTo("你个**");

        // 自定义词库：append 追加 / override 整体替换
        SensitiveWordFilter appended = SensitiveWordFilter.builder()
                .appendWords(SensitiveWordCategory.INSULT, List.of("测试专属词"))
                .build();
        assertThat(appended.detect("这是测试专属词")).hasSize(1);

        SensitiveWordFilter override = SensitiveWordFilter.builder()
                .overrideWords(List.of(new WordEntry("测试专属词", SensitiveWordCategory.PORNO)))
                .build();
        assertThat(override.detect("你个傻逼 测试专属词")).singleElement()
                .extracting(SensitiveWordHit::category)
                .isEqualTo(SensitiveWordCategory.PORNO);
    }
}
