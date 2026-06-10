package com.octopus.demo.common.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAuditLoggerTest {

    private InMemoryAuditLogger logger;

    @BeforeEach
    void setUp() {
        logger = new InMemoryAuditLogger();
    }

    @Test
    @DisplayName("log stores event, findAll retrieves it")
    void log_and_findAll() {
        var event = AuditEvent.of(1L, "CREATE", "USER", "1");
        logger.log(event);

        var all = logger.findAll();
        assertEquals(1, all.size());
        assertEquals("CREATE", all.get(0).action());
    }

    @Test
    @DisplayName("query filters by userId")
    void query_filtersByUserId() {
        logger.log(AuditEvent.of(1L, "CREATE", "USER", "1"));
        logger.log(AuditEvent.of(2L, "UPDATE", "USER", "2"));
        logger.log(AuditEvent.of(1L, "DELETE", "USER", "3"));

        var result = logger.query(AuditQuery.byUser(1L));
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("query filters by action")
    void query_filtersByAction() {
        logger.log(AuditEvent.of(1L, "CREATE", "USER", "1"));
        logger.log(AuditEvent.of(1L, "DELETE", "USER", "2"));

        var result = logger.query(AuditQuery.byAction("DELETE"));
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).entityId());
    }

    @Test
    @DisplayName("query filters by time range")
    void query_filtersByTimeRange() {
        var past = Instant.parse("2020-01-01T00:00:00Z");
        var now = Instant.now();

        logger.log(new AuditEvent(past, 1L, "CREATE", "USER", "1", Map.of()));
        logger.log(new AuditEvent(now, 1L, "UPDATE", "USER", "2", Map.of()));

        var result = logger.query(AuditQuery.byTimeRange(
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2027-01-01T00:00:00Z")));
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).entityId());
    }

    @Test
    @DisplayName("query respects limit parameter")
    void query_respectsLimit() {
        for (int i = 0; i < 20; i++) {
            logger.log(AuditEvent.of(1L, "CREATE", "USER", String.valueOf(i)));
        }

        var result = logger.query(new AuditQuery(null, null, null, null, null, 5));
        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("query with all() returns up to DEFAULT_LIMIT")
    void query_all_returnsUpToDefaultLimit() {
        for (int i = 0; i < 150; i++) {
            logger.log(AuditEvent.of(1L, "CREATE", "USER", String.valueOf(i)));
        }

        var result = logger.query(AuditQuery.all());
        assertEquals(100, result.size());
    }

    @Test
    @DisplayName("FIFO eviction when exceeding MAX_SIZE")
    void fifoEviction() {
        for (int i = 0; i < 10050; i++) {
            logger.log(AuditEvent.of(1L, "CREATE", "USER", String.valueOf(i)));
        }

        var all = logger.findAll();
        assertTrue(all.size() <= 10000);
        assertEquals("50", all.get(0).entityId());
    }

    @Test
    @DisplayName("concurrent writes do not throw and size stays within bounds")
    void concurrentWrites() throws Exception {
        int threads = 10;
        int writesPerThread = 2000;
        var executor = Executors.newFixedThreadPool(threads);
        var futures = new ArrayList<Future<?>>();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            futures.add(executor.submit(() -> {
                for (int i = 0; i < writesPerThread; i++) {
                    logger.log(AuditEvent.of((long) threadId, "CREATE", "USER",
                        threadId + "-" + i));
                }
            }));
        }

        for (var f : futures) f.get();
        executor.shutdown();

        var all = logger.findAll();
        assertTrue(all.size() <= 10000 + threads, "Size should not exceed MAX_SIZE + tolerance");
        assertTrue(all.size() > 0, "Should have some events");
    }
}
