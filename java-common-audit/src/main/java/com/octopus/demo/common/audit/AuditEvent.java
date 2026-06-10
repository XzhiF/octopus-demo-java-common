package com.octopus.demo.common.audit;

import java.time.Instant;
import java.util.Map;

/**
 * 审计事件不可变记录。
 *
 * @param timestamp  事件发生时间 (UTC, Instant 确保跨服务时间线一致)
 * @param userId     操作人 ID (null 表示系统操作)
 * @param action     操作类型: CREATE, UPDATE, DELETE, ASSIGN_ROLE 等
 * @param entityType 操作对象类型: USER, ADDRESS 等 (后续可扩展 ROLE, RESOURCE)
 * @param entityId   操作对象 ID
 * @param details    附加详情 (不可变 Map)
 */
public record AuditEvent(
    Instant timestamp,
    Long userId,
    String action,
    String entityType,
    String entityId,
    Map<String, String> details
) {
    public static AuditEvent of(Long userId, String action, String entityType, String entityId) {
        return new AuditEvent(Instant.now(), userId, action, entityType, entityId, Map.of());
    }

    public static AuditEvent of(Long userId, String action, String entityType, String entityId, Map<String, String> details) {
        return new AuditEvent(Instant.now(), userId, action, entityType, entityId, details != null ? Map.copyOf(details) : Map.of());
    }
}
