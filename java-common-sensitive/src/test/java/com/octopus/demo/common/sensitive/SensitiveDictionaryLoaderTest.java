package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringReader;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SensitiveDictionaryLoader")
class SensitiveDictionaryLoaderTest {

    @Test
    @DisplayName("built-in dictionaries provide at least 20 entries per category")
    void builtInDictionariesHaveEnoughEntries() {
        Map<String, SensitiveCategory> words = SensitiveDictionaryLoader.loadBuiltIn();

        Map<SensitiveCategory, Long> counts = words.values().stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        assertThat(counts.get(SensitiveCategory.ABUSE)).isGreaterThanOrEqualTo(20);
        assertThat(counts.get(SensitiveCategory.PORN)).isGreaterThanOrEqualTo(20);
    }

    @Test
    @DisplayName("parses category|word lines, ignoring comments and blank lines")
    void parsesValidLines() {
        Map<String, SensitiveCategory> words = SensitiveDictionaryLoader.parse(
                new StringReader("# comment\n\nABUSE|蠢货\nporn|裸聊\n"), "inline");

        assertThat(words).containsEntry("蠢货", SensitiveCategory.ABUSE);
        assertThat(words).containsEntry("裸聊", SensitiveCategory.PORN);
    }

    @Test
    @DisplayName("rejects a line without a category separator")
    void rejectsMissingSeparator() {
        assertThatThrownBy(() -> SensitiveDictionaryLoader.parse(
                new StringReader("ABUSE|ok\nbroken line\n"), "bad.txt"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bad.txt:2")
                .hasMessageContaining("broken line");
    }

    @Test
    @DisplayName("rejects an unknown category")
    void rejectsUnknownCategory() {
        assertThatThrownBy(() -> SensitiveDictionaryLoader.parse(
                new StringReader("BOGUS|word\n"), "bad.txt"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bad.txt:1");
    }

    @Test
    @DisplayName("rejects an empty word")
    void rejectsEmptyWord() {
        assertThatThrownBy(() -> SensitiveDictionaryLoader.parse(
                new StringReader("ABUSE|\n"), "bad.txt"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bad.txt:1");
    }
}
