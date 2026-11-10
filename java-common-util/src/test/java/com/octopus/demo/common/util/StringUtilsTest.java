package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StringUtilsTest {

    @Test
    @DisplayName("null returns null")
    void capitalize_null() {
        assertThat(StringUtils.capitalize(null)).isNull();
    }

    @Test
    @DisplayName("empty string returns empty string")
    void capitalize_empty() {
        assertThat(StringUtils.capitalize("")).isEmpty();
    }

    @Test
    @DisplayName("ASCII: first letter capitalized, rest unchanged")
    void capitalize_ascii() {
        assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");
        assertThat(StringUtils.capitalize("hELLO")).isEqualTo("HELLO");
    }

    @Test
    @DisplayName("BMP-outside char (surrogate pair) titlecased by code point, pair not broken")
    void capitalize_supplementary() {
        // U+10428 (𐐨) = DESERET SMALL LETTER LONG I; code-point titlecase
        // is U+10400 (𐐀) = CAPITAL LONG I. spec.md's example wrote U+10408,
        // but UnicodeData/JDK 21 Character.getName assigns U+10408 to
        // CAPITAL SHORT A — the spec's pair was mislabeled. The AC requires
        // code-point titlecase without breaking the surrogate pair.
        String result = StringUtils.capitalize("𐐨ello");
        assertThat(result).isEqualTo("𐐀ello");
        assertThat(result).hasSize(6);
        assertThat(Character.isHighSurrogate(result.charAt(0))).isTrue();
        assertThat(Character.isLowSurrogate(result.charAt(1))).isTrue();
        assertThat(Character.codePointAt(result, 0)).isEqualTo(0x10400);
    }

    @Test
    @DisplayName("special titlecase digraph: ǆ U+01C6 → ǅ U+01C5 (not all-caps Ǆ)")
    void capitalize_digraph() {
        assertThat(StringUtils.capitalize("ǆuraps")).isEqualTo("ǅuraps");
    }
}
