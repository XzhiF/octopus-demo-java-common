package com.octopus.demo.common.audit;

import com.octopus.demo.common.auth.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditContextTest {

    @AfterEach
    void tearDown() {
        AuditContext.clear();
        UserContext.clear();
    }

    @Test
    @DisplayName("getCurrentUserId delegates to UserContext")
    void getCurrentUserId_delegatesToUserContext() {
        assertNull(AuditContext.getCurrentUserId());
        UserContext.setUserId(42L);
        assertEquals(42L, AuditContext.getCurrentUserId());
    }

    @Test
    @DisplayName("setCurrentAction and getCurrentAction work correctly")
    void currentAction_setAndGet() {
        assertNull(AuditContext.getCurrentAction());
        AuditContext.setCurrentAction("CREATE");
        assertEquals("CREATE", AuditContext.getCurrentAction());
    }

    @Test
    @DisplayName("clear removes both CURRENT_ACTION and UserContext")
    void clear_removesActionAndUserContext() {
        UserContext.setUserId(1L);
        AuditContext.setCurrentAction("DELETE");

        AuditContext.clear();

        assertNull(AuditContext.getCurrentAction());
        assertNull(AuditContext.getCurrentUserId());
    }
}
