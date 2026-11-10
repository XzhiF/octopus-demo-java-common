package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Curates the built-in dictionaries: each category must have 20–30 unique, correctly
 * categorized entries with no accidental filler. English entries stay at least 4 characters
 * to avoid short-substring false positives (see {@link FalsePositiveGuardTest}).
 */
@DisplayName("Built-in dictionary curation")
class DictionaryCurationTest {

    @Test
    @DisplayName("abuse dictionary has 20-30 unique ABUSE entries")
    void abuseDictionary() throws Exception {
        assertCuration("sensitive/abuse.txt", SensitiveCategory.ABUSE);
    }

    @Test
    @DisplayName("porn dictionary has 20-30 unique PORN entries")
    void pornDictionary() throws Exception {
        assertCuration("sensitive/porn.txt", SensitiveCategory.PORN);
    }

    @Test
    @DisplayName("no word is duplicated across categories")
    void noCrossCategoryDuplicates() throws Exception {
        List<String[]> all = new ArrayList<>();
        all.addAll(readEntries("sensitive/abuse.txt"));
        all.addAll(readEntries("sensitive/porn.txt"));

        Set<String> seen = new HashSet<>();
        for (String[] entry : all) {
            String word = entry[1].toLowerCase(Locale.ROOT);
            assertThat(seen.add(word)).as("duplicate word across dictionaries: %s", entry[1])
                    .isTrue();
        }
    }

    @Test
    @DisplayName("every English entry is at least 4 characters after normalization")
    void englishEntriesAreLongEnough() throws Exception {
        List<String[]> all = new ArrayList<>();
        all.addAll(readEntries("sensitive/abuse.txt"));
        all.addAll(readEntries("sensitive/porn.txt"));

        for (String[] entry : all) {
            if (entry[1].chars().allMatch(c -> c < 128)) {
                String normalized = SensitiveTextNormalizer.normalize(entry[1]);
                assertThat(normalized.length())
                        .as("English entry \"%s\" must be >= 4 chars", entry[1])
                        .isGreaterThanOrEqualTo(4);
            }
        }
    }

    private void assertCuration(String path, SensitiveCategory expectedCategory) throws Exception {
        List<String[]> entries = readEntries(path);
        assertThat(entries).as("entry count in %s", path).hasSizeBetween(20, 30);

        Set<String> seen = new HashSet<>();
        for (String[] entry : entries) {
            assertThat(entry[0]).as("category in %s for \"%s\"", path, entry[1])
                    .isEqualTo(expectedCategory.name());
            assertThat(entry[1]).as("word in %s", path).isNotBlank();
            assertThat(seen.add(entry[1].toLowerCase(Locale.ROOT)))
                    .as("duplicate word \"%s\" in %s", entry[1], path)
                    .isTrue();
        }
    }

    private List<String[]> readEntries(String classpath) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(classpath);
        assertThat(stream).as("classpath resource %s", classpath).isNotNull();

        List<String[]> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('|');
                assertThat(separator).as("separator in line \"%s\"", trimmed).isGreaterThan(0);
                String category = trimmed.substring(0, separator).trim().toUpperCase(Locale.ROOT);
                String word = trimmed.substring(separator + 1).trim();
                entries.add(new String[] {category, word});
            }
        }
        return entries;
    }
}
