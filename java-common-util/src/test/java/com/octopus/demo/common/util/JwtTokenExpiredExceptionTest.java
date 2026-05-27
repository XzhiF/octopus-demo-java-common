package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenExpiredExceptionTest {

    @Test
    void extendsBaseException() {
        JwtTokenExpiredException ex = new JwtTokenExpiredException();
        assertInstanceOf(BaseException.class, ex);
    }

    @Test
    void codeIs401() {
        JwtTokenExpiredException ex = new JwtTokenExpiredException();
        assertEquals(401, ex.getCode());
    }

    @Test
    void messageIsTokenExpired() {
        JwtTokenExpiredException ex = new JwtTokenExpiredException();
        assertEquals("JWT token expired", ex.getMessage());
    }

    @Test
    void causeConstructor_preservesCause() {
        Throwable originalCause = new RuntimeException("original");
        JwtTokenExpiredException ex = new JwtTokenExpiredException(originalCause);
        assertEquals(401, ex.getCode());
        assertEquals("JWT token expired", ex.getMessage());
        assertSame(originalCause, ex.getCause());
    }
}
