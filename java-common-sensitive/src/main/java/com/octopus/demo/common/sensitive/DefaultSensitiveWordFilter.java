package com.octopus.demo.common.sensitive;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable, thread-safe {@link SensitiveWordFilter} that matches normalized dictionary
 * entries as substrings of normalized input text. Longer entries are evaluated first so the
 * reported category is stable when entries overlap.
 */
public final class DefaultSensitiveWordFilter implements SensitiveWordFilter {

    private final Entry[] entries;

    public DefaultSensitiveWordFilter(Map<String, SensitiveCategory> words) {
        Map<String, SensitiveCategory> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, SensitiveCategory> word : words.entrySet()) {
            String key = SensitiveTextNormalizer.normalize(word.getKey());
            if (!key.isEmpty()) {
                normalized.putIfAbsent(key, word.getValue());
            }
        }
        this.entries = normalized.entrySet().stream()
                .map(e -> new Entry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt((Entry e) -> e.word().length()).reversed()
                        .thenComparing(Entry::word))
                .toArray(Entry[]::new);
    }

    @Override
    public SensitiveCheckResult check(String text) {
        String normalizedText = SensitiveTextNormalizer.normalize(text);
        if (normalizedText.isEmpty()) {
            return SensitiveCheckResult.notHit();
        }
        for (Entry entry : entries) {
            if (normalizedText.contains(entry.word())) {
                return SensitiveCheckResult.hit(entry.category());
            }
        }
        return SensitiveCheckResult.notHit();
    }

    private record Entry(String word, SensitiveCategory category) {
    }
}
