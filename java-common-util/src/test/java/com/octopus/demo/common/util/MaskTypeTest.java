package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskTypeTest {

    @Test
    @DisplayName("exactly six constants in priority order")
    void constants_inPriorityOrder() {
        assertThat(MaskType.values()).containsExactly(
                MaskType.PHONE,
                MaskType.EMAIL,
                MaskType.ID_CARD,
                MaskType.BANK_CARD,
                MaskType.NAME,
                MaskType.ADDRESS);
    }
}
