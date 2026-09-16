package com.octopus.demo.common.util;

/**
 * Mobile phone number utility: validation ({@link #isMobile(String)}) and
 * masking ({@link #mask(String)}).
 *
 * <p>Hard constraint: never throws. Invalid (non-null) input yields {@code false}
 * from {@code isMobile} or a same-length all-'*' string from {@code mask};
 * null input yields {@code false} / {@code ""} respectively.
 */
public final class PhoneUtils {

    private static final int MOBILE_LENGTH = 11;

    private PhoneUtils() {}

    /**
     * Returns true only for mainland-China mobile numbers: exactly 11 digits,
     * starting with '1', second digit 3-9. Everything else (incl. null) is false.
     */
    public static boolean isMobile(String phone) {
        if (phone == null || phone.length() != MOBILE_LENGTH) {
            return false;
        }
        if (phone.charAt(0) != '1') {
            return false;
        }
        char second = phone.charAt(1);
        if (second < '3' || second > '9') {
            return false;
        }
        for (int i = 2; i < MOBILE_LENGTH; i++) {
            char c = phone.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Masks a valid mobile number in 3-4-4 form, e.g. {@code "13812345678"} →
     * {@code "138****5678"}. Any other input is replaced by an all-'*' string of
     * the same length; null returns "".
     */
    public static String mask(String phone) {
        if (phone == null) {
            return "";
        }
        if (isMobile(phone)) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        StringBuilder stars = new StringBuilder(phone.length());
        for (int i = 0; i < phone.length(); i++) {
            stars.append('*');
        }
        return stars.toString();
    }
}
