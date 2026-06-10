package com.octopus.demo.common.audit;

import java.util.List;

/**
 * 审计日志记录器接口。
 * 各服务提供自己的实现 (InMemory / DB / MQ)。
 *
 * <p>实现契约:
 * <ul>
 *   <li>{@link #log(AuditEvent)} 必须是线程安全的，可能被多个请求线程并发调用</li>
 *   <li>{@link #query(AuditQuery)} 返回符合查询条件的不可变快照列表，按插入顺序排列</li>
 *   <li>{@link #findAll()} 返回当前所有审计事件的快照副本</li>
 * </ul>
 */
public interface AuditLogger {
    /**
     * 记录一条审计事件。实现必须是线程安全的。
     *
     * @param event 不可变审计事件
     */
    void log(AuditEvent event);

    /**
     * 按条件查询审计事件，返回不可变快照列表。
     * 结果按插入顺序排列，受 limit 参数约束。
     *
     * @param query 查询参数 (null 字段不过滤)
     * @return 匹配的审计事件列表
     */
    List<AuditEvent> query(AuditQuery query);

    /**
     * 返回当前所有审计事件的快照副本。
     *
     * @return 全部审计事件列表
     */
    List<AuditEvent> findAll();
}
