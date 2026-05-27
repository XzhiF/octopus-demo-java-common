package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtConfigTest {

    @Test
    @DisplayName("valid config creation succeeds")
    void validConfig_creationSucceeds() {
        JwtConfig config = new JwtConfig("octopus-jwt-secret-key-default!!", 30);
        assertThat(config.secretKey()).isEqualTo("octopus-jwt-secret-key-default!!");
        assertThat(config.expirationDays()).isEqualTo(30);
    }

    @Test
    @DisplayName("expirationDays zero throws IllegalArgumentException")
    void expirationDaysZero_throwsException() {
        assertThatThrownBy(() -> new JwtConfig("key", 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("expirationDays must be > 0");
    }

    @Test
    @DisplayName("expirationDays negative throws IllegalArgumentException")
    void expirationDaysNegative_throwsException() {
        assertThatThrownBy(() -> new JwtConfig("key", -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("expirationDays must be > 0");
    }

    @Test
    @DisplayName("null secretKey is accepted for partial update")
    void nullSecretKey_accepted() {
        JwtConfig config = new JwtConfig(null, 7);
        assertThat(config.secretKey()).isNull();
        assertThat(config.expirationDays()).isEqualTo(7);
    }
}