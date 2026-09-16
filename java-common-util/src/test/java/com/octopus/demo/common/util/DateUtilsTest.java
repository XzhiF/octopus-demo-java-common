package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class DateUtilsTest {

    // ---------- 正常路径（US1–US3） ----------

    @Test
    @DisplayName("formatDate renders fixed pattern yyyy-MM-dd HH:mm:ss (US1)")
    void formatDate_normalValue_fixedPattern() {
        assertThat(DateUtils.formatDate(LocalDateTime.of(2026, 9, 16, 10, 30, 0)))
            .isEqualTo("2026-09-16 10:30:00");
    }

    @Test
    @DisplayName("parseDate parses US2 example value to matching LocalDateTime")
    void parseDate_exampleValue_returnsLocalDateTime() {
        assertThat(DateUtils.parseDate("2026-09-16 10:30:00"))
            .isEqualTo(LocalDateTime.of(2026, 9, 16, 10, 30, 0));
    }

    @Test
    @DisplayName("formatDate / parseDate round-trip is consistent")
    void formatParse_roundTrip_consistent() {
        LocalDateTime original = LocalDateTime.of(2026, 9, 16, 10, 30, 0);
        String formatted = DateUtils.formatDate(original);
        assertThat(DateUtils.parseDate(formatted)).isEqualTo(original);
    }

    @Test
    @DisplayName("daysBetween is positive when a < b (US3)")
    void daysBetween_aBeforeB_positive() {
        assertThat(DateUtils.daysBetween(
            LocalDate.of(2026, 9, 16), LocalDate.of(2026, 9, 19))).isEqualTo(3);
    }

    @Test
    @DisplayName("daysBetween is zero when a == b")
    void daysBetween_equalDates_zero() {
        LocalDate d = LocalDate.of(2026, 9, 16);
        assertThat(DateUtils.daysBetween(d, d)).isZero();
    }

    @Test
    @DisplayName("daysBetween is signed negative when a > b (US3 explicit requirement)")
    void daysBetween_aAfterB_negative() {
        assertThat(DateUtils.daysBetween(
            LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 16))).isEqualTo(-3);
    }

    @Test
    @DisplayName("isWeekend is true for Saturday (2026-09-19)")
    void isWeekend_saturday_true() {
        assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 19))).isTrue();
    }

    @Test
    @DisplayName("isWeekend is true for Sunday (2026-09-20)")
    void isWeekend_sunday_true() {
        assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 20))).isTrue();
    }

    @Test
    @DisplayName("isWeekend is false for Friday (2026-09-18)")
    void isWeekend_friday_false() {
        assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 18))).isFalse();
    }

    @Test
    @DisplayName("isWeekend is false for Monday (2026-09-14)")
    void isWeekend_monday_false() {
        assertThat(DateUtils.isWeekend(LocalDate.of(2026, 9, 14))).isFalse();
    }

    // ---------- 异常纪律路径（KD2：不抛 + 返回约定值） ----------

    @Test
    @DisplayName("KD2: formatDate(null) never throws, returns \"\"")
    void formatDate_null_returnsEmptyNeverThrows() {
        assertThatCode(() -> DateUtils.formatDate(null)).doesNotThrowAnyException();
        assertThat(DateUtils.formatDate(null)).isEqualTo("");
    }

    @Test
    @DisplayName("KD2: parseDate(null) never throws, returns null")
    void parseDate_null_returnsNullNeverThrows() {
        assertThatCode(() -> DateUtils.parseDate(null)).doesNotThrowAnyException();
        assertThat(DateUtils.parseDate(null)).isNull();
    }

    @Test
    @DisplayName("KD2: parseDate of empty text never throws, returns null")
    void parseDate_emptyText_returnsNullNeverThrows() {
        assertThatCode(() -> DateUtils.parseDate("")).doesNotThrowAnyException();
        assertThat(DateUtils.parseDate("")).isNull();
    }

    @Test
    @DisplayName("KD2: parseDate of out-of-range components never throws, returns null")
    void parseDate_outOfRangeComponents_returnsNullNeverThrows() {
        assertThatCode(() -> DateUtils.parseDate("2026-13-45 99:99:99")).doesNotThrowAnyException();
        assertThat(DateUtils.parseDate("2026-13-45 99:99:99")).isNull();
    }

    @Test
    @DisplayName("KD2: parseDate of non-date text never throws, returns null")
    void parseDate_garbageText_returnsNullNeverThrows() {
        assertThatCode(() -> DateUtils.parseDate("not-a-date")).doesNotThrowAnyException();
        assertThat(DateUtils.parseDate("not-a-date")).isNull();
    }

    @Test
    @DisplayName("KD2: daysBetween(null, d) never throws, returns 0")
    void daysBetween_nullA_returnsZeroNeverThrows() {
        LocalDate d = LocalDate.of(2026, 9, 16);
        assertThatCode(() -> DateUtils.daysBetween(null, d)).doesNotThrowAnyException();
        assertThat(DateUtils.daysBetween(null, d)).isZero();
    }

    @Test
    @DisplayName("KD2: daysBetween(d, null) never throws, returns 0")
    void daysBetween_nullB_returnsZeroNeverThrows() {
        LocalDate d = LocalDate.of(2026, 9, 16);
        assertThatCode(() -> DateUtils.daysBetween(d, null)).doesNotThrowAnyException();
        assertThat(DateUtils.daysBetween(d, null)).isZero();
    }

    @Test
    @DisplayName("KD2: isWeekend(null) never throws, returns false")
    void isWeekend_null_returnsFalseNeverThrows() {
        assertThatCode(() -> DateUtils.isWeekend(null)).doesNotThrowAnyException();
        assertThat(DateUtils.isWeekend(null)).isFalse();
    }
}
