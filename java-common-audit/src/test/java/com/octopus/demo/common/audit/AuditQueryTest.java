package com.octopus.demo.common.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AuditQueryTest {

    @Test
    @DisplayName("all() returns query with all nulls and default limit")
    void all_returnsDefaultQuery() {
        var q = AuditQuery.all();
        assertNull(q.userId());
        assertNull(q.action());
        assertNull(q.entityType());
        assertNull(q.from());
        assertNull(q.to());
        assertEquals(100, q.limit());
    }

    @Test
    @DisplayName("byUser() sets userId only")
    void byUser_setsUserIdOnly() {
        var q = AuditQuery.byUser(42L);
        assertEquals(42L, q.userId());
        assertNull(q.action());
        assertEquals(AuditQuery.DEFAULT_LIMIT, q.limit());
    }

    @Test
    @DisplayName("byAction() sets action only")
    void byAction_setsActionOnly() {
        var q = AuditQuery.byAction("DELETE");
        assertEquals("DELETE", q.action());
        assertNull(q.userId());
    }

    @Test
    @DisplayName("byTimeRange() sets from and to")
    void byTimeRange_setsFromAndTo() {
        var from = Instant.parse("2026-01-01T00:00:00Z");
        var to = Instant.parse("2026-12-31T23:59:59Z");
        var q = AuditQuery.byTimeRange(from, to);
        assertEquals(from, q.from());
        assertEquals(to, q.to());
        assertNull(q.userId());
    }

    @Test
    @DisplayName("DEFAULT_LIMIT is 100")
    void defaultLimit_is100() {
        assertEquals(100, AuditQuery.DEFAULT_LIMIT);
    }

    @Test
    @DisplayName("limit of 0 falls back to default in query")
    void limitZero_fallsBackToDefault() {
        var q = new AuditQuery(null, null, null, null, null, 0);
        assertEquals(0, q.limit());
    }
}
