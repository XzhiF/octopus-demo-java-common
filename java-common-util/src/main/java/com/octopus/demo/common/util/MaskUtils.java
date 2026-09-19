package com.octopus.demo.common.util;

/**
 * Stateless masking utility for PII display (phone numbers, ID card numbers).
 * Contract: illegal input (null / empty / shorter than the expected format) is
 * returned as-is; no exception is thrown (lenient contract, consistent with MD5Utils style).
 */
public final class MaskUtils {

    private MaskUtils() {}

    /**
     * Mask a mainland-China 11-digit mobile number, keeping the first 3 and last 4 digits:
     * {@code 13812345678 -> 138****5678}. Input shorter than 11 chars is returned unchanged.
     */
    public static String maskPhone(String phone) {
        return mask(phone, 3, 4, 11);
    }

    /**
     * Mask an 18-digit ID card number, keeping the first 6 and last 4 digits:
     * {@code 110101199003078888 -> 110101********8888}. Input shorter than 18 chars is returned unchanged.
     */
    public static String maskIdCard(String idCard) {
        return mask(idCard, 6, 4, 18);
    }

    private static String mask(String value, int keepHead, int keepTail, int minLength) {
        if (value == null || value.length() < minLength) {
            return value;
        }
        return value.substring(0, keepHead)
            + "*".repeat(value.length() - keepHead - keepTail)
            + value.substring(value.length() - keepTail);
    }
}
