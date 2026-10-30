package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTokenizerTest {

    private final Tokenizer tokenizer = Tokenizer.defaultTokenizer();

    @Test
    @DisplayName("lowercases latin words and drops punctuation")
    void tokenize_latinAndPunctuation() {
        assertThat(tokenizer.tokenize("Hello, WORLD!"))
            .containsExactly("hello", "world");
    }

    @Test
    @DisplayName("splits latin/digits from CJK into per-character tokens")
    void tokenize_mixedCjkAndLatin() {
        assertThat(tokenizer.tokenize("我爱Java"))
            .containsExactly("我", "爱", "java");
    }

    @Test
    @DisplayName("underscore separates tokens, digits stay attached")
    void tokenize_underscoreIsSeparator() {
        assertThat(tokenizer.tokenize("a1_b2"))
            .containsExactly("a1", "b2");
    }

    @Test
    @DisplayName("mixed Chinese and English sentence")
    void tokenize_chineseEnglishMixed() {
        assertThat(tokenizer.tokenize("中文 English 混合"))
            .containsExactly("中", "文", "english", "混", "合");
    }

    @Test
    @DisplayName("empty and null input yield empty list")
    void tokenize_emptyAndNull() {
        assertThat(tokenizer.tokenize("")).isEmpty();
        assertThat(tokenizer.tokenize(null)).isEmpty();
    }

    @Test
    @DisplayName("supplementary plane CJK code point stays one token")
    void tokenize_supplementaryPlaneCjk() {
        String text = new String(Character.toChars(0x20000));
        assertThat(tokenizer.tokenize(text))
            .containsExactly(text);
    }

    @Test
    @DisplayName("combining accent does not split the buffer")
    void tokenize_combiningMarkKeptWithBase() {
        assertThat(tokenizer.tokenize("é"))
            .containsExactly("é");
    }

    @Test
    @DisplayName("combining accent kept inside a longer word")
    void tokenize_combiningMarkInsideWord() {
        assertThat(tokenizer.tokenize("Café Bar"))
            .containsExactly("café", "bar");
    }

    @Test
    @DisplayName("whitespace-only input yields empty list")
    void tokenize_whitespaceOnly() {
        assertThat(tokenizer.tokenize("   \t\n")).isEmpty();
    }

    @Test
    @DisplayName("hiragana, katakana and hangul tokenize per character")
    void tokenize_otherCjkScripts() {
        assertThat(tokenizer.tokenize("ひらがなカタカナ한글"))
            .containsExactly("ひ", "ら", "が", "な", "カ", "タ", "カ", "ナ", "한", "글");
    }

    @Test
    @DisplayName("default tokenizer is a singleton")
    void defaultTokenizer_isSingleton() {
        assertThat(Tokenizer.defaultTokenizer())
            .isSameAs(Tokenizer.defaultTokenizer());
    }

    @Test
    @DisplayName("latin lowercasing uses Locale.ROOT")
    void tokenize_turkishCapitalI() {
        List<String> tokens = tokenizer.tokenize("Istanbul");
        assertThat(tokens).containsExactly("istanbul");
    }
}
