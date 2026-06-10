package com.octopus.demo.common.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditEventTest {

    @Test
    @DisplayName("of() factory sets timestamp automatically")
    void of_setsTimestampAutomatically() {
        var before = Instant.now();
        var event = AuditEvent.of(1L, "CREATE", "USER", "42");
        var after = Instant.now();

        assertNotNull(event.timestamp());
        assertFalse(event.timestamp().isBefore(before));
        assertFalse(event.timestamp().isAfter(after));
        assertEquals(1L, event.userId());
        assertEquals("CREATE", event.action());
        assertEquals("USER", event.entityType());
        assertEquals("42", event.entityId());
        assertTrue(event.details().isEmpty());
    }

    @Test
    @DisplayName("of() with details creates immutable copy")
    void of_withDetails_createsImmutableCopy() {
        var details = Map.of("key", "value");
        var event = AuditEvent.of(1L, "UPDATE", "USER", "1", details);

        assertEquals("value", event.details().get("key"));
        assertThrows(UnsupportedOperationException.class, () -> event.details().put("x", "y"));
    }

    @Test
    @DisplayName("of() with null details produces empty map")
    void of_withNullDetails_producesEmptyMap() {
        var event = AuditEvent.of(1L, "DELETE", "USER", "1", null);
        assertTrue(event.details().isEmpty());
    }

    @Test
    @DisplayName("record equality works correctly")
    void recordEquality() {
        var ts = Instant.now();
        var e1 = new AuditEvent(ts, 1L, "CREATE", "USER", "1", Map.of());
        var e2 = new AuditEvent(ts, 1L, "CREATE", "USER", "1", Map.of());
        assertEquals(e1, e2);
    }
}
