package com.octopus.demo.common.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RTest {

    @Test
    void ok_withoutData_returnsSuccessResponse() {
        R<Void> r = R.ok();
        assertEquals(200, r.getCode());
        assertNull(r.getData());
        assertEquals("success", r.getMsg());
        assertTrue(r.getTimestamp() > 0);
    }

    @Test
    void ok_withData_returnsSuccessResponseWithData() {
        R<String> r = R.ok("hello");
        assertEquals(200, r.getCode());
        assertEquals("hello", r.getData());
        assertEquals("success", r.getMsg());
        assertTrue(r.getTimestamp() > 0);
    }

    @Test
    void fail_withMessage_returnsDefaultErrorCode() {
        R<Void> r = R.fail("something wrong");
        assertEquals(500, r.getCode());
        assertNull(r.getData());
        assertEquals("something wrong", r.getMsg());
        assertTrue(r.getTimestamp() > 0);
    }

    @Test
    void fail_withCodeAndMessage_returnsCustomErrorCode() {
        R<Void> r = R.fail(4001, "param invalid");
        assertEquals(4001, r.getCode());
        assertNull(r.getData());
        assertEquals("param invalid", r.getMsg());
        assertTrue(r.getTimestamp() > 0);
    }

    @Test
    void timestamp_isSetAtCreationTime() {
        long before = System.currentTimeMillis();
        R<String> r = R.ok("test");
        long after = System.currentTimeMillis();
        assertTrue(r.getTimestamp() >= before && r.getTimestamp() <= after);
    }
}