package com.octopus.demo.common.audit;

import java.time.Instant;

/**
 * 审计日志查询参数。过滤字段可选 (null 不过滤)。
 * limit 控制返回最大条数，默认 100，防止 OOM。
 */
public record AuditQuery(
    Long userId,
    String action,
    String entityType,
    Instant from,
    Instant to,
    int limit
) {
    public static final int DEFAULT_LIMIT = 100;

    public static AuditQuery all() {
        return new AuditQuery(null, null, null, null, null, DEFAULT_LIMIT);
    }

    public static AuditQuery byUser(Long userId) {
        return new AuditQuery(userId, null, null, null, null, DEFAULT_LIMIT);
    }

    public static AuditQuery byAction(String action) {
        return new AuditQuery(null, action, null, null, null, DEFAULT_LIMIT);
    }

    public static AuditQuery byTimeRange(Instant from, Instant to) {
        return new AuditQuery(null, null, null, from, to, DEFAULT_LIMIT);
    }
}
