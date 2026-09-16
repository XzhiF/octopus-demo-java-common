package com.octopus.demo.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date/time utility built on pure JDK {@code java.time} — no external dependencies.
 *
 * <p>Fixed pattern {@value #PATTERN} (KD1: single constant, no multi-format configuration).
 *
 * <p>Null/invalid-input discipline (KD2, hard constraint): this class NEVER throws on bad
 * input so it cannot break a logging chain — {@link #formatDate} returns {@code ""},
 * {@link #parseDate} returns {@code null}, {@link #isWeekend} returns {@code false},
 * {@link #daysBetween} returns {@code 0}.
 */
public final class DateUtils {

    /** The single fixed date-time pattern (KD1). */
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private DateUtils() {}

    /**
     * Formats a date-time as {@value #PATTERN}.
     *
     * @param dateTime value to format, may be null
     * @return formatted string, or {@code ""} if input is null or formatting fails (KD2)
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return dateTime.format(FORMATTER);
        } catch (RuntimeException e) {
            // KD2: never propagate any formatting failure
            return "";
        }
    }

    /**
     * Parses a date-time string in the {@value #PATTERN} format.
     *
     * @param text string to parse, may be null or malformed
     * @return parsed value, or {@code null} if input is null/empty/unparseable (KD2)
     */
    public static LocalDateTime parseDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (RuntimeException e) {
            // KD2: DateTimeParseException (and any other parse failure) → null, never throws
            return null;
        }
    }

    /**
     * Signed day difference from {@code a} to {@code b} (negative when {@code b < a}).
     *
     * @param a start date, may be null
     * @param b end date, may be null
     * @return {@code ChronoUnit.DAYS.between(a, b)}, or {@code 0} if either input is null (KD2)
     */
    public static long daysBetween(LocalDate a, LocalDate b) {
        if (a == null || b == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(a, b);
    }

    /**
     * Weekend check: Saturday and Sunday are {@code true}.
     *
     * @param date date to check, may be null
     * @return true for SATURDAY/SUNDAY, false otherwise or if input is null (KD2)
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
