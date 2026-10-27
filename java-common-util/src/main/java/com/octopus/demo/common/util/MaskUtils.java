package com.octopus.demo.common.util;

/**
 * Sensitive-data masking utility for log output and UI echo.
 * Zero external dependencies (pure JDK).
 * Invalid or unexpected input is returned as-is — masking never throws.
 */
public final class MaskUtils {

    private static final int PHONE_LENGTH = 11;
    private static final String PHONE_MASK = "****";
    private static final String LOCAL_MASK = "***";

    private MaskUtils() {}

    /**
     * Masks an 11-digit mainland China phone number: first 3 + **** + last 4.
     * e.g. {@code 13812345678 -> 138****5678}.
     * Null, empty, wrong length or non-digit input is returned unchanged.
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != PHONE_LENGTH) return phone;
        for (int i = 0; i < PHONE_LENGTH; i++) {
            char c = phone.charAt(i);
            if (c < '0' || c > '9') return phone;
        }
        return phone.substring(0, 3) + PHONE_MASK + phone.substring(7);
    }

    /**
     * Masks the local part of an email, keeping the domain intact.
     * e.g. {@code alice@example.com -> a***e@example.com}.
     * Local part of length <= 2 is replaced entirely with {@code ***}.
     * Null, empty or input without {@code @} is returned unchanged.
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) return email;
        int at = email.indexOf('@');
        if (at < 0) return email;
        String local = email.substring(0, at);
        String domain = email.substring(at);
        if (local.length() <= 2) return LOCAL_MASK + domain;
        return local.charAt(0) + LOCAL_MASK + local.charAt(local.length() - 1) + domain;
    }
}
