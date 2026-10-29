package com.octopus.demo.common.util;

/**
 * 自定义词库条目：词 + 显式类别（KD9），用于
 * {@link SensitiveWordFilter.Builder#overrideWords} 整体覆盖默认词库。
 *
 * @param word     敏感词原文
 * @param category 所属类别
 */
public record WordEntry(String word, SensitiveWordCategory category) {
}
