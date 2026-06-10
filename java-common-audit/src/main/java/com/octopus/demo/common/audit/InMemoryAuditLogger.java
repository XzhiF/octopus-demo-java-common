package com.octopus.demo.common.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内存审计日志存储，线程安全，容量上限 10000 条，FIFO 淘汰。
 * AtomicInteger + ConcurrentLinkedDeque 组合在高并发下可能存在轻微竞态
 * (多个线程同时淘汰可能导致 size 略低于 MAX_SIZE)，对 Demo 项目可接受。
 */
public class InMemoryAuditLogger implements AuditLogger {

    private static final int MAX_SIZE = 10000;
    private final ConcurrentLinkedDeque<AuditEvent> events = new ConcurrentLinkedDeque<>();
    private final AtomicInteger size = new AtomicInteger(0);

    @Override
    public void log(AuditEvent event) {
        events.addLast(event);
        int currentSize = size.incrementAndGet();
        while (currentSize > MAX_SIZE) {
            if (events.pollFirst() != null) {
                currentSize = size.decrementAndGet();
            } else {
                break;
            }
        }
    }

    @Override
    public List<AuditEvent> query(AuditQuery query) {
        int effectiveLimit = query.limit() > 0
            ? Math.min(query.limit(), MAX_SIZE)
            : AuditQuery.DEFAULT_LIMIT;
        return events.stream()
            .filter(e -> query.userId() == null || query.userId().equals(e.userId()))
            .filter(e -> query.action() == null || query.action().equals(e.action()))
            .filter(e -> query.entityType() == null || query.entityType().equals(e.entityType()))
            .filter(e -> query.from() == null || !e.timestamp().isBefore(query.from()))
            .filter(e -> query.to() == null || !e.timestamp().isAfter(query.to()))
            .limit(effectiveLimit)
            .toList();
    }

    @Override
    public List<AuditEvent> findAll() {
        return new ArrayList<>(events);
    }
}
