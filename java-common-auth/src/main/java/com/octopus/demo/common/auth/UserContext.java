package com.octopus.demo.common.auth;

/**
 * ThreadLocal wrapper for current user identity.
 * Set by AuthInterceptor, cleared in afterCompletion to prevent thread-pool reuse leaks.
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private UserContext() {}

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
