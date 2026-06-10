package com.octopus.demo.common.audit;

import com.octopus.demo.common.auth.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 审计拦截器。
 * preHandle: 从 X-User-Id 请求头解析 userId 并写入 UserContext，
 *            确保非 @RequireAuth 端点（如 CRUD 接口）的审计事件也能记录操作人。
 * afterCompletion: 清除 AuditContext CURRENT_ACTION 和 UserContext，
 *                  防止线程池复用时数据泄漏。
 */
public class AuditInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditInterceptor.class);
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        // Only set UserContext if not already set by AuthInterceptor,
        // avoiding overwriting authenticated user identity
        if (UserContext.getUserId() == null) {
            String userIdStr = request.getHeader(USER_ID_HEADER);
            if (userIdStr != null && !userIdStr.isBlank()) {
                try {
                    long userId = Long.parseLong(userIdStr.trim());
                    if (userId > 0) {
                        UserContext.setUserId(userId);
                    }
                } catch (NumberFormatException e) {
                    log.debug("Invalid X-User-Id header value: {}", userIdStr);
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AuditContext.clear();
        UserContext.clear();
    }
}
