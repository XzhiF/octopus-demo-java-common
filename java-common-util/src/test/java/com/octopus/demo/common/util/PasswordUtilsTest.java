package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilsTest {

    private static final String PASSWORD = "Passw0rd!123";
    private static final String ENCODED_PATTERN = "pbkdf2\\$210000-[A-Za-z0-9+/]+\\$[A-Za-z0-9+/]+";

    @Test
    void shouldEncodeInPbkdf2Format() {
        String encoded = PasswordUtils.encode(PASSWORD);

        assertTrue(encoded.matches(ENCODED_PATTERN), "unexpected encoded value: " + encoded);
        assertEquals(3, encoded.split("\\$").length);
    }

    @Test
    void shouldMatchCorrectPassword() {
        String encoded = PasswordUtils.encode(PASSWORD);

        assertTrue(PasswordUtils.matches(PASSWORD, encoded));
    }

    @Test
    void shouldNotMatchWrongPassword() {
        String encoded = PasswordUtils.encode(PASSWORD);

        assertFalse(PasswordUtils.matches("WrongPassw0rd!", encoded));
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        String first = PasswordUtils.encode(PASSWORD);
        String second = PasswordUtils.encode(PASSWORD);

        assertNotEquals(first, second);
        assertTrue(PasswordUtils.matches(PASSWORD, first));
        assertTrue(PasswordUtils.matches(PASSWORD, second));
    }

    @Test
    void shouldReturnFalseForNullInputs() {
        assertFalse(PasswordUtils.matches(null, PasswordUtils.encode(PASSWORD)));
        assertFalse(PasswordUtils.matches(PASSWORD, null));
    }

    @Test
    void shouldReturnFalseForEmptyInputs() {
        assertFalse(PasswordUtils.matches("", PasswordUtils.encode(PASSWORD)));
        assertFalse(PasswordUtils.matches(PASSWORD, ""));
    }

    @Test
    void shouldReturnFalseForMalformedEncoding() {
        assertFalse(PasswordUtils.matches(PASSWORD, "not-a-pbkdf2-value"));
        assertFalse(PasswordUtils.matches(PASSWORD, "pbkdf2$abc$def"));
        assertFalse(PasswordUtils.matches(PASSWORD, "pbkdf2$210000-$"));
        assertFalse(PasswordUtils.matches(PASSWORD, "pbkdf2$0-c2FsdA$aGFzaA"));
    }

    @Test
    void shouldThrowWhenEncodingNull() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtils.encode(null));
    }
}
