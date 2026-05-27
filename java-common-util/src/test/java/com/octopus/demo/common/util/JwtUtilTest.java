package com.octopus.demo.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private static final String TEST_KEY_32 = "test-secret-key-for-hmac-sha256!";
    private static final String TEST_KEY_ALT = "alt-secret-key-for-hmac-sha256!!";
    private static final String DEFAULT_KEY = "octopus-jwt-secret-key-default!!";

    @BeforeEach
    void resetConfig() {
        JwtUtil.update(new JwtConfig(DEFAULT_KEY, 30));
    }

    @Test
    @DisplayName("generateToken returns non-empty string")
    void generateToken_returnsNonEmptyString() {
        String token = JwtUtil.generateToken(1L);
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("parseToken returns userId from generated token")
    void parseToken_returnsUserId() {
        String token = JwtUtil.generateToken(42L);
        Long userId = JwtUtil.parseToken(token);
        assertThat(userId).isEqualTo(42L);
    }

    @Test
    @DisplayName("generate and parse with custom key via update works")
    void generateAndParse_withCustomKey_works() {
        String key = JwtUtil.generateRandomSecretKeyString();
        JwtUtil.update(new JwtConfig(key, 30));
        String token = JwtUtil.generateToken(100L);
        Long userId = JwtUtil.parseToken(token);
        assertThat(userId).isEqualTo(100L);
    }

    @Test
    @DisplayName("token from one key cannot be parsed by another key")
    void generateAndParse_differentKeys_fails() {
        JwtUtil.update(new JwtConfig(TEST_KEY_32, 30));
        String token = JwtUtil.generateToken(1L);
        JwtUtil.update(new JwtConfig(TEST_KEY_ALT, 30));
        assertThatThrownBy(() -> JwtUtil.parseToken(token))
            .isInstanceOf(JwtTokenInvalidException.class);
    }

    @Test
    @DisplayName("generateToken with zero userId throws IllegalArgumentException")
    void generateToken_zeroUserId_throwsException() {
        assertThatThrownBy(() -> JwtUtil.generateToken(0L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("generateToken with negative userId throws IllegalArgumentException")
    void generateToken_negativeUserId_throwsException() {
        assertThatThrownBy(() -> JwtUtil.generateToken(-1L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("parseToken with expired token throws JwtTokenExpiredException")
    void parseToken_expiredToken_throwsExpiredException() {
        JwtUtil.update(new JwtConfig(TEST_KEY_32, -1));
        String token = JwtUtil.generateToken(1L);
        assertThatThrownBy(() -> JwtUtil.parseToken(token))
            .isInstanceOf(JwtTokenExpiredException.class);
    }

    @Test
    @DisplayName("parseToken with null token throws IllegalArgumentException")
    void parseToken_nullToken_throwsException() {
        assertThatThrownBy(() -> JwtUtil.parseToken(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("parseToken with empty token throws IllegalArgumentException")
    void parseToken_emptyToken_throwsException() {
        assertThatThrownBy(() -> JwtUtil.parseToken(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("parseToken with malformed token throws JwtTokenInvalidException")
    void parseToken_malformedToken_throwsInvalidException() {
        assertThatThrownBy(() -> JwtUtil.parseToken("not-a-token"))
            .isInstanceOf(JwtTokenInvalidException.class)
            .hasMessage("JWT token invalid");
    }

    @Test
    @DisplayName("isTokenExpired with valid token returns false")
    void isTokenExpired_validToken_returnsFalse() {
        String token = JwtUtil.generateToken(1L);
        assertThat(JwtUtil.isTokenExpired(token)).isFalse();
    }

    @Test
    @DisplayName("isTokenExpired with expired token returns true")
    void isTokenExpired_expiredToken_returnsTrue() {
        JwtUtil.update(new JwtConfig(TEST_KEY_32, -1));
        String token = JwtUtil.generateToken(1L);
        assertThat(JwtUtil.isTokenExpired(token)).isTrue();
    }

    @Test
    @DisplayName("isTokenExpired with malformed token throws JwtTokenInvalidException")
    void isTokenExpired_malformedToken_throwsInvalidException() {
        assertThatThrownBy(() -> JwtUtil.isTokenExpired("bad-token"))
            .isInstanceOf(JwtTokenInvalidException.class);
    }

    @Test
    @DisplayName("update with short secretKey throws IllegalArgumentException")
    void update_shortSecretKey_throwsException() {
        assertThatThrownBy(() -> JwtUtil.update(new JwtConfig("short-key", 30)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("32");
    }

    @Test
    @DisplayName("generateToken with large userId works")
    void generateToken_largeUserId_works() {
        long largeId = Long.MAX_VALUE;
        String token = JwtUtil.generateToken(largeId);
        assertThat(JwtUtil.parseToken(token)).isEqualTo(largeId);
    }

    @Test
    @DisplayName("parseToken expired exception preserves cause")
    void parseToken_expiredToken_preservesCause() {
        JwtUtil.update(new JwtConfig(TEST_KEY_32, -1));
        String token = JwtUtil.generateToken(1L);
        JwtTokenExpiredException ex = catchThrowableOfType(
            () -> JwtUtil.parseToken(token), JwtTokenExpiredException.class);
        assertThat(ex.getCause()).isNotNull();
    }

    @Test
    @DisplayName("parseToken invalid exception preserves cause")
    void parseToken_invalidToken_preservesCause() {
        JwtTokenInvalidException ex = catchThrowableOfType(
            () -> JwtUtil.parseToken("not-a-token"), JwtTokenInvalidException.class);
        assertThat(ex.getCause()).isNotNull();
    }

    @Test
    @DisplayName("parseToken with non-numeric subject throws JwtTokenInvalidException")
    void parseToken_tokenWithNonNumericSubject_throwsInvalidException() {
        SecretKey key = Keys.hmacShaKeyFor(TEST_KEY_32.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("not-a-number")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(key)
                .compact();
        JwtUtil.update(new JwtConfig(TEST_KEY_32, 30));
        assertThatThrownBy(() -> JwtUtil.parseToken(token))
            .isInstanceOf(JwtTokenInvalidException.class)
            .hasMessage("Invalid userId in token");
    }

    @Test
    @DisplayName("default config works without update")
    void defaultConfig_worksWithoutUpdate() {
        String token = JwtUtil.generateToken(1L);
        assertThat(JwtUtil.parseToken(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("update null config throws NullPointerException")
    void update_nullConfig_throwsException() {
        assertThatThrownBy(() -> JwtUtil.update(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("JwtConfig");
    }
}