package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtPropertiesTest {

    @Test
    void defaultSecretKeyIsEmpty() {
        JwtProperties props = new JwtProperties();
        assertEquals("", props.getSecretKey());
    }

    @Test
    void defaultExpirationDaysIs30() {
        JwtProperties props = new JwtProperties();
        assertEquals(30, props.getExpirationDays());
    }

    @Test
    void setterAndGetterWork() {
        JwtProperties props = new JwtProperties();
        props.setSecretKey("test-key");
        props.setExpirationDays(7);
        assertEquals("test-key", props.getSecretKey());
        assertEquals(7, props.getExpirationDays());
    }
}