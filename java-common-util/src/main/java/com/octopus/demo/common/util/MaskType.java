package com.octopus.demo.common.util;

/**
 * Categories of sensitive data recognized by {@link MaskUtils#maskText(String)}.
 * Declaration order is the priority order used when resolving overlapping matches.
 */
public enum MaskType {
    PHONE,
    EMAIL,
    ID_CARD,
    BANK_CARD,
    NAME,
    ADDRESS
}
