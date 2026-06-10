package com.octopus.demo.common.audit;

import com.octopus.demo.common.auth.UserContext;

/**
 * 审计上下文门面类。
 * userId 复用 UserContext.getUserId()，避免重复 ThreadLocal。
 * CURRENT_ACTION 由业务代码可选设置。
 *
 * 注意：虚拟线程环境下 ThreadLocal 可正常工作，但需注意 pinning 问题。
 * 如启用 spring.threads.virtual.enabled=true，建议后续迁移至 ScopedValue。
 */
public final class AuditContext {

    private static final ThreadLocal<String> CURRENT_ACTION = new ThreadLocal<>();

    private AuditContext() {}

    public static Long getCurrentUserId() {
        return UserContext.getUserId();
    }

    public static void setCurrentAction(String action) {
        CURRENT_ACTION.set(action);
    }

    public static String getCurrentAction() {
        return CURRENT_ACTION.get();
    }

    public static void clear() {
        CURRENT_ACTION.remove();
        UserContext.clear();
    }
}
