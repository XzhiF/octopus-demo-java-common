package com.octopus.demo.common.auth;

import com.octopus.demo.common.util.JwtConfig;
import com.octopus.demo.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuthInterceptorTest {

    private static final String TEST_SECRET = "octopus-auth-test-secret-key-0123456789";

    private final AuthInterceptor interceptor = new AuthInterceptor();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    void setUp() {
        JwtUtil.update(new JwtConfig(TEST_SECRET, 30));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void preHandle_noAnnotation_passesThrough() throws Exception {
        request.addHeader("Authorization", "Bearer " + JwtUtil.generateToken(42L));
        HandlerMethod handler = new HandlerMethod(new NoAnnotationController(), "noAuthMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_nonHandlerMethod_passesThrough() throws Exception {
        boolean result = interceptor.preHandle(request, response, "not-a-handler-method");
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_validToken_setsUserIdAndPasses() throws Exception {
        String token = JwtUtil.generateToken(42L);
        request.addHeader("Authorization", "Bearer " + token);
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(42L, UserContext.getUserId());
    }

    @Test
    void preHandle_lowercaseBearerPrefix_isAccepted() throws Exception {
        String token = JwtUtil.generateToken(7L);
        request.addHeader("Authorization", "bearer " + token);
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(7L, UserContext.getUserId());
    }

    @Test
    void preHandle_tokenWithSurroundingWhitespace_isTrimmed() throws Exception {
        String token = JwtUtil.generateToken(42L);
        request.addHeader("Authorization", "  Bearer   " + token + "  ");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(42L, UserContext.getUserId());
    }

    @Test
    void preHandle_missingToken_requiredTrue_returns401WithMissingRequiredToken() throws Exception {
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing required token"));
    }

    @Test
    void preHandle_blankAuthorization_requiredTrue_returns401WithMissingRequiredToken() throws Exception {
        request.addHeader("Authorization", "   ");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing required token"));
    }

    @Test
    void preHandle_onlyXUserIdHeader_returns401() throws Exception {
        request.addHeader("X-User-Id", "42");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_invalidToken_returns401WithInvalidToken() throws Exception {
        request.addHeader("Authorization", "Bearer not-a-valid-jwt");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid token"));
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_nonBearerScheme_returns401WithInvalidToken() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid token"));
    }

    @Test
    void preHandle_bearerPrefixWithoutToken_returns401WithInvalidToken() throws Exception {
        request.addHeader("Authorization", "Bearer   ");
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid token"));
    }

    @Test
    void preHandle_expiredToken_returns401WithTokenExpired() throws Exception {
        request.addHeader("Authorization", "Bearer " + expiredToken(42L));
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token expired"));
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_validToken_signedWithDifferentKey_returns401WithInvalidToken() throws Exception {
        String otherKey = "octopus-auth-other-secret-key-9876543210";
        String token = Jwts.builder()
                .subject("42")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(otherKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
        request.addHeader("Authorization", "Bearer " + token);
        HandlerMethod handler = new HandlerMethod(new RequiredTrueController(), "handleMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid token"));
    }

    @Test
    void preHandle_requiredFalse_noToken_passesWithoutSetting() throws Exception {
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_requiredFalse_validToken_setsUserIdAndPasses() throws Exception {
        request.addHeader("Authorization", "Bearer " + JwtUtil.generateToken(99L));
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertEquals(99L, UserContext.getUserId());
    }

    @Test
    void preHandle_requiredFalse_invalidToken_returns401() throws Exception {
        request.addHeader("Authorization", "Bearer not-a-valid-jwt");
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid token"));
    }

    @Test
    void preHandle_requiredFalse_expiredToken_returns401() throws Exception {
        request.addHeader("Authorization", "Bearer " + expiredToken(42L));
        HandlerMethod handler = new HandlerMethod(new RequiredFalseController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token expired"));
    }

    @Test
    void preHandle_methodOverridesClassRequired() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ClassRequiredTrueController(), "optionalMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        assertNull(UserContext.getUserId());
    }

    @Test
    void preHandle_classRequiredFalse_methodRequiredTrue_noToken_returns401() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ClassRequiredFalseController(), "requiredMethod");
        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing required token"));
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
        assertTrue(body.contains("401"));
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

    private String expiredToken(long userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(now - 7_200_000))
                .expiration(new Date(now - 3_600_000))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
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
