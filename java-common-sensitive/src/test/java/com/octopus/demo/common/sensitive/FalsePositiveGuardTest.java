package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Guards against false positives of the built-in dictionaries. Any English/Chinese entry that
 * would fire on these legitimate words must be removed or lengthened — see dictionary curation.
 */
@DisplayName("False-positive guards")
class FalsePositiveGuardTest {

    private final SensitiveWordFilter filter =
            new DefaultSensitiveWordFilter(SensitiveDictionaryLoader.loadBuiltIn());

    @Test
    @DisplayName("class-family words do not match")
    void classFamily() {
        assertNoHit("class", "classes", "classic", "classify", "classroom");
    }

    @Test
    @DisplayName("assignment-family words do not match")
    void assignmentFamily() {
        assertNoHit("assignment", "assignments", "assign");
    }

    @Test
    @DisplayName("passenger does not match")
    void passenger() {
        assertNoHit("passenger", "passengers");
    }

    @Test
    @DisplayName("Scunthorpe and similar place names do not match")
    void scunthorpe() {
        assertNoHit("Scunthorpe", "scunthorpe", "Scunthorpe United");
    }

    @Test
    @DisplayName("legitimate words containing a dictionary term as a substring do not match")
    void legitimateSubstringCarriers() {
        assertNoHit("foolish", "foolproof", "wretched", "wretchedness");
    }

    @Test
    @DisplayName("ordinary English nicknames do not match")
    void ordinaryEnglishNicknames() {
        assertNoHit("E2E_Alice_2026", "grasshopper", "classic_gamer", "sunshine");
    }

    @Test
    @DisplayName("ordinary Chinese nicknames do not match")
    void ordinaryChineseNicknames() {
        assertNoHit("E2E_小明", "小明", "张三丰", "白杨树");
    }

    private void assertNoHit(String... words) {
        for (String word : words) {
            SensitiveCheckResult result = filter.check(word);
            assertThat(result.hit()).as("expected no hit for \"%s\"", word).isFalse();
            assertThat(result.category()).as("expected null category for \"%s\"", word).isNull();
        }
    }
}
