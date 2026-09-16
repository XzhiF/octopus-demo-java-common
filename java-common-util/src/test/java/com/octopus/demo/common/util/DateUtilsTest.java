package com.octopus.demo.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Boundary tests for {@link DateUtils}: normal paths (US1–US3) and
 * the never-throw discipline (KD2).
 */
class DateUtilsTest {

    private static final LocalDateTime SAMPLE = LocalDateTime.of(2026, 9, 16, 10, 30, 0);
    private static final String SAMPLE_TEXT = "2026-09-16 10:30:00";

    @Nested
    @DisplayName("正常路径 US1-US3")
    class HappyPath {

        @Test
        void formatDateProducesFixedPattern() {
            assertThat(DateUtils.formatDate(SAMPLE)).isEqualTo(SAMPLE_TEXT);
        }

        @Test
        void parseDateReturnsLocalDateTime() {
            assertThat(DateUtils.parseDate(SAMPLE_TEXT)).isEqualTo(SAMPLE);
        }

        @Test
        void formatParseRoundTrip() {
            assertThat(DateUtils.parseDate(DateUtils.formatDate(SAMPLE))).isEqualTo(SAMPLE);
            assertThat(DateUtils.formatDate(DateUtils.parseDate(SAMPLE_TEXT))).isEqualTo(SAMPLE_TEXT);
        }

        @Test
        void daysBetweenIsSigned() {
            LocalDate a = LocalDate.of(2026, 9, 1);
            LocalDate b = LocalDate.of(2026, 9, 16);
            assertThat(DateUtils.daysBetween(a, b)).isEqualTo(15L);   // a < b → 正
            assertThat(DateUtils.daysBetween(a, a)).isZero();          // a == b → 0
            assertThat(DateUtils.daysBetween(b, a)).isEqualTo(-15L);   // a > b → 负
        }

        @Test
        void isWeekendMatchesSaturdaySunday() {
            assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 19))).isTrue();   // 周六
            assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 20))).isTrue();   // 周日
            assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 18))).isFalse();  // 周五
            assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 21))).isFalse();  // 周一
        }
    }

    @Nested
    @DisplayName("KD2 异常纪律：永不抛 + 约定返回值")
    class NeverThrows {

        @Test
        void formatDateNullReturnsEmptyString() {
            assertThatCode(() -> DateUtils.formatDate(null)).doesNotThrowAnyException();
            assertThat(DateUtils.formatDate(null)).isEmpty();
        }

        @Test
        void parseDateNullReturnsNull() {
            assertThatCode(() -> DateUtils.parseDate(null)).doesNotThrowAnyException();
            assertThat(DateUtils.parseDate(null)).isNull();
        }

        @Test
        void parseDateInvalidInputsReturnNull() {
            assertThatCode(() -> DateUtils.parseDate("2026-13-45 99:99:99")).doesNotThrowAnyException();
            assertThatCode(() -> DateUtils.parseDate("not-a-date")).doesNotThrowAnyException();
            assertThatCode(() -> DateUtils.parseDate("")).doesNotThrowAnyException();
            assertThatCode(() -> DateUtils.parseDate("   ")).doesNotThrowAnyException();
            assertThat(DateUtils.parseDate("2026-13-45 99:99:99")).isNull();
            assertThat(DateUtils.parseDate("not-a-date")).isNull();
            assertThat(DateUtils.parseDate("")).isNull();
            assertThat(DateUtils.parseDate("   ")).isNull();
        }

        @Test
        void isWeekendNullReturnsFalse() {
            assertThatCode(() -> DateUtils.isWeekend(null)).doesNotThrowAnyException();
            assertThat(DateUtils.isWeekend(null)).isFalse();
        }

        @Test
        void daysBetweenNullInputsReturnZero() {
            LocalDate d = LocalDate.of(2026, 9, 16);
            assertThatCode(() -> DateUtils.daysBetween(null, d)).doesNotThrowAnyException();
            assertThatCode(() -> DateUtils.daysBetween(d, null)).doesNotThrowAnyException();
            assertThat(DateUtils.daysBetween(null, d)).isZero();
            assertThat(DateUtils.daysBetween(d, null)).isZero();
        }
    }
}
