package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AEDConfigTest {

    @Test
    @DisplayName("valid config creation succeeds")
    void validConfig_creationSucceeds() {
        AEDConfig config = new AEDConfig("octopus-aes-128!!", "octopus-aes-iv16!");
        assertThat(config.secretKey()).isEqualTo("octopus-aes-128!!");
        assertThat(config.ivParameter()).isEqualTo("octopus-aes-iv16!");
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
        AEDConfig config = new AEDConfig("my-16byte-key!!", null);
        assertThat(config.secretKey()).isEqualTo("my-16byte-key!!");
        assertThat(config.ivParameter()).isNull();
    }
}