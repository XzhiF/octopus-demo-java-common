package com.octopus.demo.common.audit;

import com.octopus.demo.common.auth.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditInterceptorTest {

    private AuditInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new AuditInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @AfterEach
    void tearDown() {
        AuditContext.clear();
        UserContext.clear();
    }

    @Test
    @DisplayName("preHandle sets UserContext from X-User-Id header when not already set")
    void preHandle_setsUserContextFromHeader() {
        when(request.getHeader("X-User-Id")).thenReturn("42");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(42L, UserContext.getUserId());
    }

    @Test
    @DisplayName("preHandle does not overwrite UserContext if already set by AuthInterceptor")
    void preHandle_doesNotOverwriteExistingUserContext() {
        UserContext.setUserId(99L);
        when(request.getHeader("X-User-Id")).thenReturn("42");

        interceptor.preHandle(request, response, new Object());

        assertEquals(99L, UserContext.getUserId(), "Should not overwrite existing UserContext");
    }

    @Test
    @DisplayName("preHandle ignores missing X-User-Id header")
    void preHandle_ignoresMissingHeader() {
        when(request.getHeader("X-User-Id")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    @DisplayName("preHandle ignores blank X-User-Id header")
    void preHandle_ignoresBlankHeader() {
        when(request.getHeader("X-User-Id")).thenReturn("  ");

        interceptor.preHandle(request, response, new Object());

        assertNull(UserContext.getUserId());
    }

    @Test
    @DisplayName("preHandle ignores non-numeric X-User-Id header")
    void preHandle_ignoresNonNumericHeader() {
        when(request.getHeader("X-User-Id")).thenReturn("abc");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    @DisplayName("preHandle ignores non-positive userId")
    void preHandle_ignoresNonPositiveUserId() {
        when(request.getHeader("X-User-Id")).thenReturn("-1");

        interceptor.preHandle(request, response, new Object());

        assertNull(UserContext.getUserId());
    }

    @Test
    @DisplayName("afterCompletion clears both AuditContext and UserContext")
    void afterCompletion_clearsAllContext() {
        UserContext.setUserId(42L);
        AuditContext.setCurrentAction("CREATE");

        interceptor.afterCompletion(request, response, new Object(), null);

        assertNull(UserContext.getUserId());
        assertNull(AuditContext.getCurrentAction());
    }
}
