package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes a name anchored by an explicit label ({@code 姓名 / 收件人 / 联系人 / 收货人}).
 * Only the name itself is masked; the label and surrounding text are left untouched.
 * Unlabeled names are deliberately not matched to avoid damaging ordinary Chinese text.
 */
final class NameRule implements MaskRule {

    private static final Pattern NAME = Pattern.compile(
            "(姓名|收件人|联系人|收货人)\\s*[:：]\\s*([一-龥·]{2,4})(?![一-龥])");

    @Override
    public MaskType type() {
        return MaskType.NAME;
    }

    @Override
    public List<MaskHit> find(String text) {
        List<MaskHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;
        Matcher m = NAME.matcher(text);
        while (m.find()) {
            String name = m.group(2);
            hits.add(new MaskHit(m.start(2), m.end(2), maskName(name)));
        }
        return hits;
    }

    private static String maskName(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }
}
