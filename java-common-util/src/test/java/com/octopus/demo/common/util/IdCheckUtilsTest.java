package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdCheckUtilsTest {

    @Test
    void validCardNumber() {
        assertThat(IdCheckUtils.luhn("4539578763621486")).isTrue();
    }

    @Test
    void wrongCheckDigit() {
        assertThat(IdCheckUtils.luhn("4539578763621487")).isFalse();
    }

    @Test
    void nonNumeric() {
        assertThat(IdCheckUtils.luhn("abc")).isFalse();
    }

    @Test
    void nullInput() {
        assertThat(IdCheckUtils.luhn(null)).isFalse();
    }

    @Test
    void emptyInput() {
        assertThat(IdCheckUtils.luhn("")).isFalse();
    }

    @Test
    void tooShort() {
        assertThat(IdCheckUtils.luhn("7")).isFalse();
    }

    @Test
    void spacesNotAllowed() {
        assertThat(IdCheckUtils.luhn("4539 5787 6362 1486")).isFalse();
    }
}
