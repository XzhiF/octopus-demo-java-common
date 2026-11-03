package com.octopus.demo.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octopus.demo.common.util.JwtTokenExpiredException;
import com.octopus.demo.common.util.JwtTokenInvalidException;
import com.octopus.demo.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * Intercepts requests to Controller methods/classes annotated with @RequireAuth.
 * Accepts only {@code Authorization: Bearer <JWT>}; the token is parsed by JwtUtil
 * and the resulting userId is stored in UserContext ThreadLocal.
 * Clears UserContext in afterCompletion to prevent thread-pool reuse leaks.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireAuth auth = getAuthAnnotation(handlerMethod);
        if (auth == null) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || authorization.trim().isEmpty()) {
            if (auth.required()) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Missing required token");
                return false;
            }
            return true;
        }

        String token = extractBearerToken(authorization);
        if (token == null) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Invalid token");
            return false;
        }

        try {
            Long userId = JwtUtil.parseToken(token);
            if (userId == null || userId <= 0) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                return false;
            }
            UserContext.setUserId(userId);
            return true;
        } catch (JwtTokenExpiredException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token expired");
            return false;
        } catch (JwtTokenInvalidException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Invalid token");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private String extractBearerToken(String authorization) {
        String header = authorization.trim();
        if (!header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private RequireAuth getAuthAnnotation(HandlerMethod handlerMethod) {
        RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        if (methodAuth != null) {
            return methodAuth;
        }
        return AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireAuth.class);
    }

    private void writeErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        OBJECT_MAPPER.writeValue(response.getWriter(), Map.of("code", statusCode, "msg", message));
    }
}
