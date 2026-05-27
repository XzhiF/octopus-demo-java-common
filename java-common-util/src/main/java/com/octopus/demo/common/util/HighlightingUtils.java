package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keyword highlighting utility for search results.
 * Wraps matched keywords with configurable prefix/suffix markers.
 * Default markers: &lt;em&gt;/&lt;/em&gt;.
 * Case-insensitive matching; preserves original text case in output.
 */
public final class HighlightingUtils {

    private static final String DEFAULT_PREFIX = "<em>";
    private static final String DEFAULT_SUFFIX = "</em>";

    private HighlightingUtils() {}

    public static String highlight(String text, String keyword) {
        return highlight(text, keyword, DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

    public static String highlight(String text, String keyword, String prefix, String suffix) {
        if (text == null || text.isEmpty()) return text;
        if (keyword == null || keyword.isEmpty()) return text;
        String escaped = Pattern.quote(keyword);
        Pattern pattern = Pattern.compile(escaped, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb,
                Matcher.quoteReplacement(prefix + matcher.group() + suffix));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String highlightAll(String text, Collection<String> keywords) {
        return highlightAll(text, keywords, DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

    public static String highlightAll(String text, Collection<String> keywords,
                                       String prefix, String suffix) {
        if (text == null || text.isEmpty()) return text;
        if (keywords == null || keywords.isEmpty()) return text;
        List<String> sorted = keywords.stream()
            .filter(k -> k != null && !k.isEmpty())
            .sorted((a, b) -> b.length() - a.length())
            .toList();
        // Phase 1: Replace each keyword occurrence with a unique placeholder,
        // preserving original case in a lookup map. Longer keywords are processed
        // first so shorter keywords cannot match inside already-marked segments.
        String result = text;
        Map<String, String> placeholderToReplacement = new LinkedHashMap<>();
        for (int i = 0; i < sorted.size(); i++) {
            String keyword = sorted.get(i);
            String escaped = Pattern.quote(keyword);
            Pattern pattern = Pattern.compile(escaped, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(result);
            StringBuilder sb = new StringBuilder();
            int matchIndex = 0;
            while (matcher.find()) {
                String placeholder = "" + i + "_" + matchIndex + "";
                placeholderToReplacement.put(placeholder,
                    prefix + matcher.group() + suffix);
                matcher.appendReplacement(sb,
                    Matcher.quoteReplacement(placeholder));
                matchIndex++;
            }
            matcher.appendTail(sb);
            result = sb.toString();
        }
        // Phase 2: Replace all placeholders with their actual prefix/suffix tags
        for (Map.Entry<String, String> entry : placeholderToReplacement.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}