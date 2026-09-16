package com.octopus.demo.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Date/time utility built on {@code java.time} only (no extra dependencies).
 * Fixed pattern {@value #PATTERN}. Exception discipline: never throws — invalid or
 * {@code null} input falls back to a neutral value so callers' log chains are not broken.
 */
public final class DateUtils {

    /** Single fixed pattern for format & parse (KD1). */
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private DateUtils() {}

    /**
     * Format a {@link LocalDateTime} as {@code yyyy-MM-dd HH:mm:ss}.
     *
     * @return formatted string, or {@code ""} when input is {@code null}
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return dateTime.format(FORMATTER);
        } catch (RuntimeException e) {
            return "";
        }
    }

    /**
     * Parse a {@code yyyy-MM-dd HH:mm:ss} string into a {@link LocalDateTime}.
     *
     * @return parsed value, or {@code null} when input is {@code null}/blank/unparseable
     */
    public static LocalDateTime parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Signed whole-day difference between two dates, computed as {@code b - a}.
     *
     * @return days from {@code a} to {@code b} (negative when {@code b < a}),
     *         or {@code 0} when either input is {@code null}
     */
    public static long daysBetween(LocalDate a, LocalDate b) {
        if (a == null || b == null) {
            return 0L;
        }
        try {
            return ChronoUnit.DAYS.between(a, b);
        } catch (RuntimeException e) {
            return 0L;
        }
    }

    /**
     * Check whether a date falls on Saturday or Sunday.
     *
     * @return {@code true} for SATURDAY/SUNDAY, {@code false} otherwise or when input is {@code null}
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
}
