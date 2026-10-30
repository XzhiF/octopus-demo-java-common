package com.octopus.demo.common.util;

/**
 * A single match within a text: the half-open range {@code [start, end)} and the
 * masked replacement for that range.
 */
record MaskHit(int start, int end, String replacement) {
}
