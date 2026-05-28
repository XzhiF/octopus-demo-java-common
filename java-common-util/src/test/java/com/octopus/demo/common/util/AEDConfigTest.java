package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AEDConfigTest {

    @Test
    @DisplayName("valid config creation succeeds")
    void validConfig_creationSucceeds() {
        AEDConfig config = new AEDConfig("octopus-aes-128!", "octopus-aes-iv16");
        assertThat(config.secretKey()).isEqualTo("octopus-aes-128!");
        assertThat(config.ivParameter()).isEqualTo("octopus-aes-iv16");
    }

    @Test
    @DisplayName("null fields are accepted for partial update")
    void nullFields_accepted() {
        AEDConfig config = new AEDConfig(null, null);
        assertThat(config.secretKey()).isNull();
        assertThat(config.ivParameter()).isNull();
    }

    @Test
    @DisplayName("config with only secretKey")
    void configWithOnlySecretKey() {
        AEDConfig config = new AEDConfig("0123456789abcdef", null);
        assertThat(config.secretKey()).isEqualTo("0123456789abcdef");
        assertThat(config.ivParameter()).isNull();
    }

    @Test
    @DisplayName("secretKey shorter than 16 bytes throws IllegalArgumentException")
    void secretKey_short_throwsException() {
        assertThatThrownBy(() -> new AEDConfig("short-key", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Secret key must be exactly 16 UTF-8 bytes");
    }

    @Test
    @DisplayName("secretKey longer than 16 bytes throws IllegalArgumentException")
    void secretKey_long_throwsException() {
        assertThatThrownBy(() -> new AEDConfig("this-is-a-long-key-value", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Secret key must be exactly 16 UTF-8 bytes");
    }

    @Test
    @DisplayName("ivParameter shorter than 16 bytes throws IllegalArgumentException")
    void ivParameter_short_throwsException() {
        assertThatThrownBy(() -> new AEDConfig(null, "short-iv"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("IV parameter must be exactly 16 UTF-8 bytes");
    }
}