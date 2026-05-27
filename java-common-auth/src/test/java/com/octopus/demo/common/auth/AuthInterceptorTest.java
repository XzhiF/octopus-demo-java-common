package com.octopus.demo.common.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.*;

class AuthInterceptorTest {

    private final AuthInterceptor interceptor = new AuthInterceptor();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void preHandle_noAnnotation_passesThrough() throws Exception {
        HandlerMethod handler = new HandlerMethod(new NoAnnotationController(), "noAuthMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_requiredTrue_withUserId_setsUserIdAndPasses() throws Exception {
        request.addHeader("X-User-Id", "42");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(42L, UserContext.getUserId());
    }

    @Test
    void preHandle_requiredTrue_noUserId_returns401() throws Exception {
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("401"));
        assertTrue(response.getContentAsString().contains("Missing required userId"));
    }

    @Test
    void preHandle_requiredFalse_noUserId_passesWithoutSetting() throws Exception {
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_requiredFalse_withUserId_setsUserIdAndPasses() throws Exception {
        request.addHeader("X-User-Id", "99");
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(99L, UserContext.getUserId());
    }

    @Test
    void preHandle_invalidUserIdFormat_returns400() throws Exception {
        request.addHeader("X-User-Id", "not-a-number");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("400"));
        assertTrue(response.getContentAsString().contains("Invalid userId format"));
    }

    @Test
    void preHandle_zeroUserId_returns400() throws Exception {
        request.addHeader("X-User-Id", "0");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid userId format"));
    }

    @Test
    void preHandle_negativeUserId_returns400() throws Exception {
        request.addHeader("X-User-Id", "-1");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid userId format"));
    }

    @Test
    void preHandle_headerWithWhitespace_trimsAndSetsUserId() throws Exception {
        request.addHeader("X-User-Id", "  42  ");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(42L, UserContext.getUserId());
    }

    @Test
    void preHandle_errorResponse_isValidJsonWithCodeAndMsg() throws Exception {
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        interceptor.preHandle(request, response, handler);
        String body = response.getContentAsString();
        assertTrue(body.startsWith("{"));
        assertTrue(body.endsWith("}"));
        assertTrue(body.contains("\"code\""));
        assertTrue(body.contains("\"msg\""));
    }

    @Test
    void preHandle_requiredFalse_invalidFormat_returns400() throws Exception {
        request.addHeader("X-User-Id", "not-a-number");
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid userId format"));
    }

    @Test
    void preHandle_requiredFalse_emptyStringHeader_passesWithoutSetting() throws Exception {
        request.addHeader("X-User-Id", "");
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_requiredTrue_emptyStringHeader_returns401() throws Exception {
        request.addHeader("X-User-Id", "");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandle_nonHandlerMethod_passesThrough() throws Exception {
        boolean result = interceptor.preHandle(request, response, "not-a-handler-method");
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_methodOverridesClassRequired() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ClassRequiredTrueController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_classRequiredFalse_methodRequiredTrue_noUserId_returns401() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ClassRequiredFalseController(), "requiredMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void afterCompletion_clearsUserContext() throws Exception {
        UserContext.setUserId(42L);
        interceptor.afterCompletion(request, response, new HandlerMethod(new NoAnnotationController(), "noAuthMethod"), null);
        assertNull(UserContext.getUserId());
    }

    @Test
    void afterCompletion_clearsUserContext_evenWithException() throws Exception {
        UserContext.setUserId(42L);
        interceptor.afterCompletion(request, response, new HandlerMethod(new NoAnnotationController(), "noAuthMethod"), new RuntimeException("test"));
        assertNull(UserContext.getUserId());
    }

    static class NoAnnotationController {
        public void noAuthMethod() {}
    }

    @RequireAuth
    static class RequiredTrueController {
        public void handleMethod() {}
    }

    @RequireAuth(required = false)
    static class RequiredFalseController {
        public void optionalMethod() {}
    }

    @RequireAuth
    static class ClassRequiredTrueController {
        @RequireAuth(required = false)
        public void optionalMethod() {}
    }

    @RequireAuth(required = false)
    static class ClassRequiredFalseController {
        @RequireAuth
        public void requiredMethod() {}
    }
}
