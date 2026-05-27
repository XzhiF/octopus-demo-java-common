package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenInvalidExceptionTest {

    @Test
    void extendsBaseException() {
        JwtTokenInvalidException ex = new JwtTokenInvalidException("bad signature");
        assertInstanceOf(BaseException.class, ex);
    }

    @Test
    void codeIs401() {
        JwtTokenInvalidException ex = new JwtTokenInvalidException("bad signature");
        assertEquals(401, ex.getCode());
    }

    @Test
    void preservesCustomMessage() {
        JwtTokenInvalidException ex = new JwtTokenInvalidException("invalid signature");
        assertEquals("invalid signature", ex.getMessage());
    }

    @Test
    void defaultMessageIsInvalid() {
        JwtTokenInvalidException ex = new JwtTokenInvalidException("JWT token invalid");
        assertEquals("JWT token invalid", ex.getMessage());
    }
}