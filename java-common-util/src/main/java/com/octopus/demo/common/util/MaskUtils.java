package com.octopus.demo.common.util;

/**
 * PII masking utility for display and log output: phone, ID card, email.
 */
public final class MaskUtils {

    private static final int PHONE_LENGTH = 11;
    private static final int ID_CARD_MIN_LENGTH = 8;

    private MaskUtils() {}

    /**
     * Mask an 11-digit phone number keeping the first 3 and last 4 digits:
     * {@code 13812345678 -> 138****5678}. Any other length is fully masked
     * with asterisks of the same length.
     */
    public static String maskPhone(String phone) {
        if (isBlank(phone)) {
            return phone;
        }
        if (phone.length() != PHONE_LENGTH) {
            return "*".repeat(phone.length());
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * Mask an ID card number of at least 8 characters, keeping the first 3
     * and last 2 characters with the middle fully masked. Shorter values are
     * fully masked with asterisks of the same length.
     */
    public static String maskIdCard(String idCard) {
        if (isBlank(idCard)) {
            return idCard;
        }
        if (idCard.length() < ID_CARD_MIN_LENGTH) {
            return "*".repeat(idCard.length());
        }
        return idCard.substring(0, 3)
                + "*".repeat(idCard.length() - 5)
                + idCard.substring(idCard.length() - 2);
    }

    /**
     * Mask the username part of an email address while leaving the domain
     * untouched: {@code someone@example.com -> so****e@example.com}. A username
     * of at least 3 characters keeps its first two and last one character with
     * {@code ****} in between; a 2-character username keeps its two outer
     * characters the same way; shorter usernames are fully masked. A value
     * without {@code @} is masked with the same username rules.
     */
    public static String maskEmail(String email) {
        if (isBlank(email)) {
            return email;
        }
        int at = email.indexOf('@');
        if (at < 0) {
            return maskUserName(email);
        }
        return maskUserName(email.substring(0, at)) + email.substring(at);
    }

    private static String maskUserName(String name) {
        if (name.length() < 2) {
            return "*".repeat(name.length());
        }
        if (name.length() == 2) {
            return name.substring(0, 1) + "****" + name.substring(1);
        }
        return name.substring(0, 2) + "****" + name.substring(name.length() - 1);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
