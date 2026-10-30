package com.octopus.demo.common.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Resolves overlapping matches produced by {@link MaskRule}s.
 * Out-of-range or malformed hits are dropped; this class never throws.
 */
final class MaskTextScanner {

    private static final Comparator<MaskHit> ORDER =
            Comparator.comparingInt(MaskHit::start)
                    .thenComparing(Comparator.comparingInt((MaskHit h) -> h.end() - h.start()).reversed())
                    .thenComparing(MaskHit::replacement);

    private MaskTextScanner() {}

    /**
     * Sorts hits by start ascending, length descending, then replacement, and greedily
     * keeps non-overlapping ones. Each character is masked at most once.
     */
    static List<MaskHit> resolve(List<MaskHit> hits, int textLength) {
        if (hits == null || hits.isEmpty()) return new ArrayList<>();

        List<MaskHit> valid = new ArrayList<>();
        for (MaskHit hit : hits) {
            if (hit == null) continue;
            if (hit.start() < 0 || hit.end() > textLength || hit.start() >= hit.end()) continue;
            valid.add(hit);
        }
        valid.sort(ORDER);

        List<MaskHit> accepted = new ArrayList<>();
        int lastEnd = -1;
        for (MaskHit hit : valid) {
            if (hit.start() >= lastEnd) {
                accepted.add(hit);
                lastEnd = hit.end();
            }
        }
        return accepted;
    }
}
