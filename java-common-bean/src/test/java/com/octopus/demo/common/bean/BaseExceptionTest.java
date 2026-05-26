package com.octopus.demo.common.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseExceptionTest {

    @Test
    void constructor_withCodeAndMessage_setsFields() {
        BaseException ex = new BaseException(4001, "param invalid");

        assertEquals(4001, ex.getCode());
        assertEquals("param invalid", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_withCause_propagatesCause() {
        Throwable cause = new NullPointerException("null value");
        BaseException ex = new BaseException(5001, "internal error", cause);

        assertEquals(5001, ex.getCode());
        assertEquals("internal error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void code_isFinal_andCannotBeModified() {
        BaseException ex = new BaseException(4001, "test");
        assertEquals(4001, ex.getCode());
    }

    @Test
    void exception_toRConversion_worksCorrectly() {
        BaseException ex = new BaseException(4001, "param invalid");
        R<Void> r = R.fail(ex.getCode(), ex.getMessage());

        assertEquals(4001, r.getCode());
        assertEquals("param invalid", r.getMsg());
    }
}