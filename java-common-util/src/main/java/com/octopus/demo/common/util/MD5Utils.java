package com.octopus.demo.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * MD5 hash digest utility.
 * WARNING: MD5 is cryptographically broken. Use only for checksums, data fingerprinting,
 * and legacy compatibility. Never use MD5 for password storage or security-sensitive signing.
 */
public final class MD5Utils {

    private MD5Utils() {}

    public static String md5(String input) {
        return md5(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5(byte[] input) {
        MessageDigest digest = getMessageDigest();
        byte[] hash = digest.digest(input);
        return HexFormat.of().formatHex(hash);
    }

    public static String md5(InputStream input) throws IOException {
        MessageDigest digest = getMessageDigest();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}