package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes 18-digit id cards whose ISO 7064 MOD 11-2 checksum matches.
 * The first 6 and last 4 characters are preserved, the middle 8 are masked.
 */
final class IdCardRule implements MaskRule {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\d)\\d{17}[\\dXx](?!\\d)");
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    @Override
    public MaskType type() {
        return MaskType.ID_CARD;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String id = matcher.group();
            if (hasValidChecksum(id)) {
                hits.add(new MaskHit(matcher.start(), matcher.end(),
                        id.substring(0, 6) + "********" + id.substring(14)));
            }
        }
        return hits;
    }

    private static boolean hasValidChecksum(String id) {
        int sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            sum += (id.charAt(i) - '0') * WEIGHTS[i];
        }
        return Character.toUpperCase(id.charAt(17)) == CODES[sum % 11];
    }
}
