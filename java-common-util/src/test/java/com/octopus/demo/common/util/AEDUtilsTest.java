package com.octopus.demo.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AEDUtilsTest {

    private static final String DEFAULT_KEY = "octopus-aes-128!";
    private static final String DEFAULT_IV = "octopus-aes-iv16";

    @BeforeEach
    void resetConfig() {
        AEDUtils.update(new AEDConfig(DEFAULT_KEY, DEFAULT_IV));
    }

    @Test
    @DisplayName("encrypt and decrypt round-trip with default key")
    void encryptDecrypt_defaultKey_roundTrip() {
        String original = "hello world";
        String encrypted = AEDUtils.encrypt(original);
        String decrypted = AEDUtils.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("encrypt and decrypt round-trip with custom key")
    void encryptDecrypt_customKey_roundTrip() {
        String original = "test data";
        String key = "custom-key-16chr";
        String iv = "custom-iv-16char";
        String encrypted = AEDUtils.encrypt(original, key, iv);
        String decrypted = AEDUtils.decrypt(encrypted, key, iv);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("encrypt with different key produces different ciphertext")
    void encrypt_differentKeys_differentCiphertext() {
        String input = "same input";
        String key1 = "octopus-aes-128!";
        String key2 = "anotheraes-key16";
        String iv = "octopus-aes-iv16";
        String encrypted1 = AEDUtils.encrypt(input, key1, iv);
        String encrypted2 = AEDUtils.encrypt(input, key2, iv);
        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("decrypt with wrong key throws exception")
    void decrypt_wrongKey_throwsException() {
        String original = "secret message";
        String key1 = "octopus-aes-128!";
        String key2 = "anotheraes-key16";
        String iv = "octopus-aes-iv16";
        String encrypted = AEDUtils.encrypt(original, key1, iv);
        assertThatThrownBy(() -> AEDUtils.decrypt(encrypted, key2, iv))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("update changes encryption behavior")
    void update_changesEncryptionBehavior() {
        String original = "test data";
        String encrypted1 = AEDUtils.encrypt(original);
        AEDUtils.update(new AEDConfig("new-key-16bytes!", "new-iv-16bytes!!"));
        String encrypted2 = AEDUtils.encrypt(original);
        assertThat(encrypted1).isNotEqualTo(encrypted2);
        assertThat(AEDUtils.decrypt(encrypted2)).isEqualTo(original);
    }

    @Test
    @DisplayName("encrypt with short key throws IllegalArgumentException")
    void encrypt_shortKey_throwsException() {
        assertThatThrownBy(() -> AEDUtils.encrypt("test", "short", "octopus-aes-iv16"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Secret key");
    }

    @Test
    @DisplayName("encrypt with short IV throws IllegalArgumentException")
    void encrypt_shortIv_throwsException() {
        assertThatThrownBy(() -> AEDUtils.encrypt("test", "octopus-aes-128!", "short"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("IV parameter");
    }

    @Test
    @DisplayName("encrypt with key longer than 16 bytes throws IllegalArgumentException")
    void encrypt_longKey_throwsException() {
        assertThatThrownBy(() -> AEDUtils.encrypt("test", "this-is-too-long-key", "octopus-aes-iv16"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Secret key");
    }

    @Test
    @DisplayName("encrypt produces Base64 output")
    void encrypt_producesBase64() {
        String encrypted = AEDUtils.encrypt("hello");
        assertThat(encrypted).matches("[A-Za-z0-9+/]+=*");
    }

    @Test
    @DisplayName("encrypt and decrypt with Chinese characters")
    void encryptDecrypt_chineseCharacters() {
        String original = "章鱼加密测试";
        String encrypted = AEDUtils.encrypt(original);
        String decrypted = AEDUtils.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("update with null config throws NullPointerException")
    void update_nullConfig_throwsException() {
        assertThatThrownBy(() -> AEDUtils.update(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("AEDConfig");
    }
}