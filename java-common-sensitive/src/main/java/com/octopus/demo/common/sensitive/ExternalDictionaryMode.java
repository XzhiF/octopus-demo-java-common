package com.octopus.demo.common.sensitive;

/**
 * How an external dictionary combines with the built-in dictionaries.
 */
public enum ExternalDictionaryMode {
    /** Merge external entries on top of the built-in entries. */
    APPEND,
    /** Use only external entries; built-in entries are discarded. */
    REPLACE
}
