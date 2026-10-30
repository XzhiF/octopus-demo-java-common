package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes addresses by a double anchor: an administrative chain
 * ({@code 省/自治区/市} + {@code 市/区/县/旗}) followed by a detailed-address feature
 * within the next 60 characters. The administrative chain is kept verbatim and everything
 * after the last district/county-level unit is replaced with {@code ****}.
 * Bare district names or admin chains without a detail feature are not matched.
 */
final class AddressRule implements MaskRule {

    private static final String ADMIN =
            "[一-龥]{1,6}?(?:省|自治区|市)[一-龥]{1,6}?(?:市|区|县|旗)(?:[一-龥]{0,6}?(?:市|区|县|旗))?";
    private static final String DETAIL =
            "[一-龥A-Za-z0-9\\-]{0,50}?(?:路|街|道|巷|号|楼|室|栋|小区|大厦|园区|村|镇)[一-龥A-Za-z0-9\\-]{0,20}";

    private static final Pattern ADDRESS =
            Pattern.compile("(" + ADMIN + ")\\s*(" + DETAIL + ")");

    @Override
    public MaskType type() {
        return MaskType.ADDRESS;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        Matcher m = ADDRESS.matcher(text);
        while (m.find()) {
            String adminPart = m.group(1);
            String masked = adminPart + "****";
            hits.add(new MaskHit(m.start(1), m.end(), masked));
        }
        return hits;
    }
}
