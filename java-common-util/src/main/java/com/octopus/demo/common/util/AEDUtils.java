package com.octopus.demo.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * AES encrypt/decrypt utility using AES/CBC/PKCS5Padding.
 * Pure static utility — configure via update(AEDConfig), use via static methods.
 * Default expiration: development-only defaults — production MUST call update().
 *
 * Thread safety: encrypt()/decrypt() without explicit key/iv are synchronized
 * to ensure secretKey and ivParameter are read atomically (they must be paired).
 * Overloads with explicit key/iv are not synchronized (caller manages the pair).
 */
public final class AEDUtils {

    private static volatile String secretKey = "octopus-aes-128!";
    private static volatile String ivParameter = "octopus-aes-iv16";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 16;
    private static final int IV_LENGTH = 16;

    private AEDUtils() {}

    public static synchronized void update(AEDConfig config) {
        Objects.requireNonNull(config, "AEDConfig must not be null");
        if (config.secretKey() != null) {
            validateLength(config.secretKey(), KEY_LENGTH, "Secret key");
            secretKey = config.secretKey();
        }
        if (config.ivParameter() != null) {
            validateLength(config.ivParameter(), IV_LENGTH, "IV parameter");
            ivParameter = config.ivParameter();
        }
    }

    public static synchronized String encrypt(String plaintext) {
        return encrypt(plaintext, secretKey, ivParameter);
    }

    public static String encrypt(String plaintext, String key, String iv) {
        validateLength(key, KEY_LENGTH, "Secret key");
        validateLength(iv, IV_LENGTH, "IV parameter");
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public static synchronized String decrypt(String ciphertext) {
        return decrypt(ciphertext, secretKey, ivParameter);
    }

    public static String decrypt(String ciphertext, String key, String iv) {
        validateLength(key, KEY_LENGTH, "Secret key");
        validateLength(iv, IV_LENGTH, "IV parameter");
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(ciphertext);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    private static void validateLength(String value, int required, String name) {
        int actual = value.getBytes(StandardCharsets.UTF_8).length;
        if (actual != required) {
            throw new IllegalArgumentException(
                name + " must be exactly " + required + " UTF-8 bytes, got " + actual);
        }
    }
}