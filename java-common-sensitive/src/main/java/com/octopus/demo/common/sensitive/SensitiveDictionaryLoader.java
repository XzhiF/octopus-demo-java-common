package com.octopus.demo.common.sensitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Loads dictionaries in {@code 类别|词} line format. Blank lines and lines starting with
 * {@code #} are ignored. Any other malformed line fails fast with the source and line number.
 */
public final class SensitiveDictionaryLoader {

    private static final List<String> BUILT_IN_RESOURCES = List.of(
            "sensitive/abuse.txt",
            "sensitive/porn.txt");

    private SensitiveDictionaryLoader() {
    }

    public static Map<String, SensitiveCategory> loadBuiltIn() {
        Map<String, SensitiveCategory> words = new LinkedHashMap<>();
        for (String path : BUILT_IN_RESOURCES) {
            words.putAll(loadResource(new ClassPathResource(path)));
        }
        return words;
    }

    public static Map<String, SensitiveCategory> loadExternal(Resource resource) {
        return loadResource(resource);
    }

    private static Map<String, SensitiveCategory> loadResource(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return parse(reader, describe(resource));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read sensitive dictionary: " + describe(resource), e);
        }
    }

    public static Map<String, SensitiveCategory> parse(Reader reader, String sourceName) {
        Map<String, SensitiveCategory> words = new LinkedHashMap<>();
        try (BufferedReader buffered = new BufferedReader(reader)) {
            String line;
            int lineNumber = 0;
            while ((line = buffered.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('|');
                if (separator < 0) {
                    throw invalid(sourceName, lineNumber, trimmed);
                }
                String categoryName = trimmed.substring(0, separator).trim();
                String word = trimmed.substring(separator + 1).trim();
                if (word.isEmpty()) {
                    throw invalid(sourceName, lineNumber, trimmed);
                }
                SensitiveCategory category = parseCategory(categoryName, sourceName, lineNumber, trimmed);
                words.put(word, category);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read sensitive dictionary: " + sourceName, e);
        }
        return words;
    }

    private static SensitiveCategory parseCategory(
            String categoryName, String sourceName, int lineNumber, String line) {
        try {
            return SensitiveCategory.valueOf(categoryName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw invalid(sourceName, lineNumber, line);
        }
    }

    private static IllegalStateException invalid(String sourceName, int lineNumber, String line) {
        return new IllegalStateException(
                "Invalid sensitive dictionary entry at " + sourceName + ":" + lineNumber + ": " + line);
    }

    private static String describe(Resource resource) {
        try {
            return resource.getDescription();
        } catch (RuntimeException e) {
            return String.valueOf(resource);
        }
    }
}
