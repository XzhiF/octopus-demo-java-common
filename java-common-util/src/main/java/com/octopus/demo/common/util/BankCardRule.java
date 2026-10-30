package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes 13–19 digit bank cards, keeping the last 4 digits.
 * Ranges claimed by {@link IdCardRule} are skipped so a valid id card is never
 * split into a bank card.
 */
final class BankCardRule implements MaskRule {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\d)\\d{13,19}(?!\\d)");
    private static final int KEEP = 4;

    @Override
    public MaskType type() {
        return MaskType.BANK_CARD;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        List<MaskHit> idCards = new IdCardRule().find(text);
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (overlaps(start, end, idCards)) continue;
            hits.add(new MaskHit(start, end, mask(matcher.group())));
        }
        return hits;
    }

    private static boolean overlaps(int start, int end, List<MaskHit> idCards) {
        for (MaskHit idCard : idCards) {
            if (start < idCard.end() && idCard.start() < end) return true;
        }
        return false;
    }

    private static String mask(String digits) {
        StringBuilder masked = new StringBuilder(digits.length());
        for (int i = 0; i < digits.length() - KEEP; i++) {
            masked.append('*');
        }
        return masked.append(digits.substring(digits.length() - KEEP)).toString();
    }
}
