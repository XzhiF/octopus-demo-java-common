package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes mainland China 11-digit mobile numbers not embedded in a longer digit run.
 */
final class PhoneRule implements MaskRule {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\d)1[3-9]\\d{9}(?!\\d)");

    @Override
    public MaskType type() {
        return MaskType.PHONE;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            hits.add(new MaskHit(matcher.start(), matcher.end(), MaskUtils.maskPhone(matcher.group())));
        }
        return hits;
    }
}
