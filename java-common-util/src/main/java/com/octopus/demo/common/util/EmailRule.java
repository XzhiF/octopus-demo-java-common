package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes email addresses and masks them via {@link MaskUtils#maskEmail(String)}.
 */
final class EmailRule implements MaskRule {

    private static final Pattern EMAIL =
            Pattern.compile("(?<![A-Za-z0-9._%+*-])[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    @Override
    public MaskType type() {
        return MaskType.EMAIL;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        Matcher m = EMAIL.matcher(text);
        while (m.find()) {
            String raw = m.group();
            if (raw.indexOf('*') >= 0) continue;
            hits.add(new MaskHit(m.start(), m.end(), MaskUtils.maskEmail(raw)));
        }
        return hits;
    }
}
