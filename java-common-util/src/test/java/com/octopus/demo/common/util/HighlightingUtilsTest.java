package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HighlightingUtilsTest {

    @Test
    @DisplayName("highlight single keyword with default em tags")
    void highlight_singleKeyword_defaultTags() {
        assertThat(HighlightingUtils.highlight("hello world", "hello"))
            .isEqualTo("<em>hello</em> world");
    }

    @Test
    @DisplayName("highlight with custom prefix and suffix")
    void highlight_customPrefixSuffix() {
        assertThat(HighlightingUtils.highlight("hello world", "world", "[", "]"))
            .isEqualTo("hello [world]");
    }

    @Test
    @DisplayName("highlight preserves original case in match")
    void highlight_preservesOriginalCase() {
        assertThat(HighlightingUtils.highlight("Hello World", "hello"))
            .isEqualTo("<em>Hello</em> World");
    }

    @Test
    @DisplayName("highlight is case insensitive")
    void highlight_caseInsensitive() {
        assertThat(HighlightingUtils.highlight("HELLO world", "hello"))
            .isEqualTo("<em>HELLO</em> world");
    }

    @Test
    @DisplayName("highlight with null text returns null")
    void highlight_nullText_returnsNull() {
        assertThat(HighlightingUtils.highlight(null, "keyword")).isNull();
    }

    @Test
    @DisplayName("highlight with empty text returns empty")
    void highlight_emptyText_returnsEmpty() {
        assertThat(HighlightingUtils.highlight("", "keyword")).isEmpty();
    }

    @Test
    @DisplayName("highlight with null keyword returns original text")
    void highlight_nullKeyword_returnsOriginal() {
        assertThat(HighlightingUtils.highlight("some text", null)).isEqualTo("some text");
    }

    @Test
    @DisplayName("highlight with empty keyword returns original text")
    void highlight_emptyKeyword_returnsOriginal() {
        assertThat(HighlightingUtils.highlight("some text", "")).isEqualTo("some text");
    }

    @Test
    @DisplayName("highlight handles regex special characters in keyword")
    void highlight_regexSpecialChars() {
        assertThat(HighlightingUtils.highlight("price is $100", "$100"))
            .isEqualTo("price is <em>$100</em>");
    }

    @Test
    @DisplayName("highlight multiple occurrences of same keyword")
    void highlight_multipleOccurrences() {
        assertThat(HighlightingUtils.highlight("hello hello hello", "hello"))
            .isEqualTo("<em>hello</em> <em>hello</em> <em>hello</em>");
    }

    @Test
    @DisplayName("highlightAll with multiple keywords")
    void highlightAll_multipleKeywords() {
        assertThat(HighlightingUtils.highlightAll("hello world foo",
            List.of("hello", "world")))
            .isEqualTo("<em>hello</em> <em>world</em> foo");
    }

    @Test
    @DisplayName("highlightAll processes longer keywords first")
    void highlightAll_longerKeywordsFirst() {
        assertThat(HighlightingUtils.highlightAll("hello world",
            List.of("hello world", "hello")))
            .isEqualTo("<em>hello world</em>");
    }

    @Test
    @DisplayName("highlightAll with custom tags")
    void highlightAll_customTags() {
        assertThat(HighlightingUtils.highlightAll("hello world",
            List.of("hello"), "**", "**"))
            .isEqualTo("**hello** world");
    }

    @Test
    @DisplayName("highlightAll with null text returns null")
    void highlightAll_nullText_returnsNull() {
        assertThat(HighlightingUtils.highlightAll(null, List.of("key"))).isNull();
    }

    @Test
    @DisplayName("highlightAll with empty keywords returns original text")
    void highlightAll_emptyKeywords_returnsOriginal() {
        assertThat(HighlightingUtils.highlightAll("text", List.of())).isEqualTo("text");
    }

    @Test
    @DisplayName("highlightAll with null keywords returns original text")
    void highlightAll_nullKeywords_returnsOriginal() {
        assertThat(HighlightingUtils.highlightAll("text", null)).isEqualTo("text");
    }

    @Test
    @DisplayName("highlight Chinese characters")
    void highlight_chineseCharacters() {
        assertThat(HighlightingUtils.highlight("章鱼科技公司", "章鱼"))
            .isEqualTo("<em>章鱼</em>科技公司");
    }
}