package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaskTextScannerTest {

    @Test
    @DisplayName("overlapping hits: earlier start wins")
    void overlapping_earlierStartWins() {
        List<MaskHit> hits = List.of(
                new MaskHit(3, 8, "B"),
                new MaskHit(1, 5, "A"));
        assertThat(MaskTextScanner.resolve(hits, 10))
                .isEqualTo(List.of(new MaskHit(1, 5, "A")));
    }

    @Test
    @DisplayName("overlapping hits with same start: longer wins")
    void overlapping_sameStart_longerWins() {
        List<MaskHit> hits = List.of(
                new MaskHit(2, 9, "short"),
                new MaskHit(2, 12, "long"));
        assertThat(MaskTextScanner.resolve(hits, 20))
                .isEqualTo(List.of(new MaskHit(2, 12, "long")));
    }

    @Test
    @DisplayName("adjacent hits are both kept")
    void adjacent_bothKept() {
        List<MaskHit> hits = List.of(
                new MaskHit(0, 5, "A"),
                new MaskHit(5, 9, "B"));
        assertThat(MaskTextScanner.resolve(hits, 10))
                .isEqualTo(List.of(new MaskHit(0, 5, "A"), new MaskHit(5, 9, "B")));
    }

    @Test
    @DisplayName("empty list returns empty list, not null")
    void empty_returnsEmptyList() {
        assertThat(MaskTextScanner.resolve(new ArrayList<>(), 10)).isEqualTo(List.of());
        assertThat(MaskTextScanner.resolve(List.of(), 10)).isNotNull();
        assertThat(MaskTextScanner.resolve(null, 10)).isEqualTo(List.of());
    }

    @Test
    @DisplayName("out-of-range hits are dropped without throwing")
    void outOfRange_dropped() {
        List<MaskHit> hits = List.of(
                new MaskHit(0, 11, "A"),
                new MaskHit(-1, 3, "B"),
                new MaskHit(4, 4, "C"),
                new MaskHit(5, 8, "D"));
        assertThat(MaskTextScanner.resolve(hits, 10))
                .isEqualTo(List.of(new MaskHit(5, 8, "D")));
    }

    @Test
    @DisplayName("non-overlapping hits are sorted by start")
    void nonOverlapping_sortedByStart() {
        List<MaskHit> hits = List.of(
                new MaskHit(6, 9, "C"),
                new MaskHit(0, 2, "A"),
                new MaskHit(3, 5, "B"));
        assertThat(MaskTextScanner.resolve(hits, 10)).isEqualTo(List.of(
                new MaskHit(0, 2, "A"),
                new MaskHit(3, 5, "B"),
                new MaskHit(6, 9, "C")));
    }
}
