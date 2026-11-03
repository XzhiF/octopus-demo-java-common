package com.octopus.demo.common.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PBKDF2-HmacSHA256 password hashing utility (zero external dependencies).
 * Encoded form: {@code pbkdf2$<iterations>-<saltBase64>$<hashBase64>}.
 */
public final class PasswordUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2$";
    private static final int DEFAULT_ITERATIONS = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private PasswordUtils() {
    }

    public static String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword must not be null");
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword, salt, DEFAULT_ITERATIONS, KEY_LENGTH_BITS);
        return PREFIX + DEFAULT_ITERATIONS + "-" + BASE64_ENCODER.encodeToString(salt)
                + "$" + BASE64_ENCODER.encodeToString(hash);
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()
                || encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        try {
            if (!encodedPassword.startsWith(PREFIX)) {
                return false;
            }
            String body = encodedPassword.substring(PREFIX.length());
            int dollar = body.indexOf('$');
            if (dollar <= 0 || body.indexOf('$', dollar + 1) >= 0) {
                return false;
            }
            String iterationsAndSalt = body.substring(0, dollar);
            int dash = iterationsAndSalt.indexOf('-');
            if (dash <= 0) {
                return false;
            }
            int iterations = Integer.parseInt(iterationsAndSalt.substring(0, dash));
            if (iterations <= 0) {
                return false;
            }
            byte[] salt = BASE64_DECODER.decode(iterationsAndSalt.substring(dash + 1));
            byte[] expected = BASE64_DECODER.decode(body.substring(dollar + 1));
            if (salt.length == 0 || expected.length == 0) {
                return false;
            }
            byte[] actual = pbkdf2(rawPassword, salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(CharSequence rawPassword, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = null;
        try {
            spec = new PBEKeySpec(rawPassword.toString().toCharArray(), salt, iterations, keyLengthBits);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException("PBKDF2 hashing failed", ex);
        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }
}
