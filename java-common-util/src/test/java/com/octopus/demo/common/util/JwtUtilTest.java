package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String TEST_KEY_32 = "test-secret-key-for-hmac-sha256!";
    private static final String TEST_KEY_ALT = "alt-secret-key-for-hmac-sha256!!";

    @Test
    void generateToken_returnsNonEmptyString() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        String token = util.generateToken(1L);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void parseToken_returnsUserId() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        String token = util.generateToken(42L);
        Long userId = util.parseToken(token);
        assertEquals(42L, userId);
    }

    @Test
    void generateAndParse_withCustomKey_works() {
        String key = JwtUtil.generateRandomSecretKeyString();
        JwtUtil util = JwtUtil.create(key, 30);
        String token = util.generateToken(100L);
        Long userId = util.parseToken(token);
        assertEquals(100L, userId);
    }

    @Test
    void generateAndParse_differentKeys_fails() {
        JwtUtil util1 = JwtUtil.create(TEST_KEY_32, 30);
        JwtUtil util2 = JwtUtil.create(TEST_KEY_ALT, 30);
        String token = util1.generateToken(1L);
        assertThrows(JwtTokenInvalidException.class, () -> util2.parseToken(token));
    }

    @Test
    void generateToken_zeroUserId_throwsException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertThrows(IllegalArgumentException.class, () -> util.generateToken(0L));
    }

    @Test
    void generateToken_negativeUserId_throwsException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertThrows(IllegalArgumentException.class, () -> util.generateToken(-1L));
    }

    @Test
    void parseToken_expiredToken_throwsExpiredException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, -1);
        String token = util.generateToken(1L);
        assertThrows(JwtTokenExpiredException.class, () -> util.parseToken(token));
    }

    @Test
    void parseToken_nullToken_throwsException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertThrows(IllegalArgumentException.class, () -> util.parseToken(null));
    }

    @Test
    void parseToken_emptyToken_throwsException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertThrows(IllegalArgumentException.class, () -> util.parseToken(""));
    }

    @Test
    void parseToken_malformedToken_throwsInvalidException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        JwtTokenInvalidException ex = assertThrows(JwtTokenInvalidException.class,
                () -> util.parseToken("not-a-token"));
        assertEquals("JWT token invalid", ex.getMessage());
    }

    @Test
    void isTokenExpired_validToken_returnsFalse() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        String token = util.generateToken(1L);
        assertFalse(util.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_expiredToken_returnsTrue() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, -1);
        String token = util.generateToken(1L);
        assertTrue(util.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_malformedToken_throwsInvalidException() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertThrows(JwtTokenInvalidException.class, () -> util.isTokenExpired("bad-token"));
    }

    @Test
    void createDefault_producesWorkingInstance() {
        JwtUtil util = JwtUtil.createDefault();
        String token = util.generateToken(1L);
        Long userId = util.parseToken(token);
        assertEquals(1L, userId);
    }

    @Test
    void createDefault_differentInstances_differentKeys() {
        JwtUtil util1 = JwtUtil.createDefault();
        JwtUtil util2 = JwtUtil.createDefault();
        String token = util1.generateToken(1L);
        assertThrows(JwtTokenInvalidException.class, () -> util2.parseToken(token));
    }

    @Test
    void create_shortSecretKey_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> JwtUtil.create("short-key", 30));
    }

    @Test
    void create_exactly32ByteKey_works() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        assertNotNull(util);
        String token = util.generateToken(1L);
        assertEquals(1L, util.parseToken(token));
    }

    @Test
    void generateToken_largeUserId_works() {
        JwtUtil util = JwtUtil.create(TEST_KEY_32, 30);
        long largeId = Long.MAX_VALUE;
        String token = util.generateToken(largeId);
        Long userId = util.parseToken(token);
        assertEquals(largeId, userId);
    }
}