package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class MD5UtilsTest {

    @Test
    @DisplayName("md5 of known string produces correct hex")
    void md5_string_knownValue() {
        assertThat(MD5Utils.md5("")).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }

    @Test
    @DisplayName("md5 of 'hello' produces correct hex")
    void md5_string_hello() {
        assertThat(MD5Utils.md5("hello")).isEqualTo("5d41402abc4b2a76b9719d911017c592");
    }

    @Test
    @DisplayName("md5 of byte array produces same result as string")
    void md5_bytes_matchesString() {
        String input = "test-input";
        String fromString = MD5Utils.md5(input);
        String fromBytes = MD5Utils.md5(input.getBytes(StandardCharsets.UTF_8));
        assertThat(fromBytes).isEqualTo(fromString);
    }

    @Test
    @DisplayName("md5 of InputStream produces correct hex")
    void md5_stream_knownValue() throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(
            "hello".getBytes(StandardCharsets.UTF_8));
        assertThat(MD5Utils.md5(stream)).isEqualTo("5d41402abc4b2a76b9719d911017c592");
    }

    @Test
    @DisplayName("md5 of Chinese characters produces consistent result")
    void md5_chineseCharacters_consistent() {
        String chinese = "章鱼";
        String result1 = MD5Utils.md5(chinese);
        String result2 = MD5Utils.md5(chinese);
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSize(32);
    }

    @Test
    @DisplayName("md5 output is lowercase hex")
    void md5_output_isLowercaseHex() {
        String result = MD5Utils.md5("test");
        assertThat(result).matches("[0-9a-f]{32}");
    }

    @Test
    @DisplayName("md5 of different inputs produces different hashes")
    void md5_differentInputs_differentHashes() {
        assertThat(MD5Utils.md5("input1")).isNotEqualTo(MD5Utils.md5("input2"));
    }
}