package com.octopus.demo.common.util;

/**
 * Sensitive value masking utility.
 *
 * <p>Pure, stateless static methods. Masking is conservative: any non-blank input is
 * masked and never returned verbatim. Asterisk counts are fixed so that the length of
 * the original value is not leaked.</p>
 */
public final class MaskUtils {

    private static final String PHONE_MASK = "****";
    private static final String EMAIL_MASK = "***";
    private static final String NICKNAME_MASK = "*";

    private MaskUtils() {}

    public static String maskPhone(String phone) {
        if (phone == null) {
            return null;
        }
        if (phone.trim().isEmpty()) {
            return phone;
        }
        if (isMainlandMobile(phone)) {
            return phone.substring(0, 3) + PHONE_MASK + phone.substring(phone.length() - 4);
        }
        int[] codePoints = phone.codePoints().toArray();
        if (codePoints.length == 1) {
            return NICKNAME_MASK;
        }
        return new String(Character.toChars(codePoints[0])) + EMAIL_MASK;
    }

    public static String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        if (email.trim().isEmpty()) {
            return email;
        }
        int at = email.indexOf('@');
        if (at > 0 && at < email.length() - 1) {
            String local = email.substring(0, at);
            String domain = email.substring(at + 1);
            int[] localCodePoints = local.codePoints().toArray();
            if (localCodePoints.length == 1) {
                return EMAIL_MASK + "@" + domain;
            }
            return new String(Character.toChars(localCodePoints[0])) + EMAIL_MASK + "@" + domain;
        }
        int[] codePoints = email.codePoints().toArray();
        if (codePoints.length == 1) {
            return NICKNAME_MASK;
        }
        return new String(Character.toChars(codePoints[0])) + EMAIL_MASK;
    }

    public static String maskNickname(String nickname) {
        if (nickname == null) {
            return null;
        }
        if (nickname.trim().isEmpty()) {
            return nickname;
        }
        int[] codePoints = nickname.codePoints().toArray();
        if (codePoints.length == 1) {
            return NICKNAME_MASK;
        }
        return new String(Character.toChars(codePoints[0])) + NICKNAME_MASK;
    }

    private static boolean isMainlandMobile(String value) {
        if (value.length() != 11 || value.charAt(0) != '1') {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
