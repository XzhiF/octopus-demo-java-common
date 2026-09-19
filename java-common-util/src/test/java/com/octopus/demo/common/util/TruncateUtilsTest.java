package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TruncateUtilsTest {

    @Test
    void nullReturnsAsIs() {
        assertThat(TruncateUtils.truncate(null, 5)).isNull();
    }

    @Test
    void emptyReturnsAsIs() {
        assertThat(TruncateUtils.truncate("", 5)).isEmpty();
    }

    @Test
    void nonPositiveMaxLenReturnsEmpty() {
        assertThat(TruncateUtils.truncate("abc", 0)).isEmpty();
        assertThat(TruncateUtils.truncate("abc", -1)).isEmpty();
    }

    @Test
    void equalLengthReturnsAsIs() {
        assertThat(TruncateUtils.truncate("abcde", 5)).isEqualTo("abcde");
    }

    @Test
    void shorterThanMaxLenReturnsAsIs() {
        assertThat(TruncateUtils.truncate("ab", 5)).isEqualTo("ab");
    }

    @Test
    void overLongTruncatedWithEllipsis() {
        String result = TruncateUtils.truncate("abcdefgh", 5);
        assertThat(result).isEqualTo("abcd…");
        assertThat(result).hasSize(5);
    }

    @Test
    void chineseTruncated() {
        String result = TruncateUtils.truncate("双仓截断链路走查", 5);
        assertThat(result).isEqualTo("双仓截断…");
        assertThat(result).hasSize(5);
    }
}
