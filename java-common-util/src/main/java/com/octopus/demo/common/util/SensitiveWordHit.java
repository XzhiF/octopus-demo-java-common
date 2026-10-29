package com.octopus.demo.common.util;

/**
 * 一次敏感词命中。
 *
 * @param word       命中的词库词
 * @param category   类别
 * @param startIndex 原文起始下标（含）
 * @param endIndex   原文结束下标（含端点）
 */
public record SensitiveWordHit(String word, SensitiveWordCategory category, int startIndex, int endIndex) {
}
