package com.octopus.demo.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * Intercepts requests to Controller methods/classes annotated with @RequireAuth.
 * Extracts userId from X-User-Id header into UserContext ThreadLocal.
 * Clears UserContext in afterCompletion to prevent thread-pool reuse leaks.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
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

        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr != null) {
            userIdStr = userIdStr.trim();
        }

        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                long userId = Long.parseLong(userIdStr);
                if (userId <= 0) {
                    writeErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "Invalid userId format");
                    return false;
                }
                UserContext.setUserId(userId);
                return true;
            } catch (NumberFormatException e) {
                writeErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "Invalid userId format");
                return false;
            }
        }

        if (auth.required()) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Missing required userId");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private RequireAuth getAuthAnnotation(HandlerMethod handlerMethod) {
        RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        if (methodAuth != null) {
            return methodAuth;
        }
        return handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
    }

    private void writeErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        OBJECT_MAPPER.writeValue(response.getWriter(), Map.of("code", statusCode, "msg", message));
    }
}
