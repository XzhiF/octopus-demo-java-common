package com.octopus.demo.common.util;

import java.util.List;

/**
 * Recognizes one category of sensitive data within free text.
 * Implementations are stateless.
 */
interface MaskRule {

    MaskType type();

    List<MaskHit> find(String text);
}
