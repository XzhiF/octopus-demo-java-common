package com.octopus.demo.common.audit;

import java.util.List;

/**
 * 审计日志记录器接口。
 * 各服务提供自己的实现 (InMemory / DB / MQ)。
 */
public interface AuditLogger {
    void log(AuditEvent event);
    List<AuditEvent> query(AuditQuery query);
    List<AuditEvent> findAll();
}
