/**
 * Authentication module providing user identity extraction via HTTP header.
 *
 * Includes:
 * - RequireAuth: annotation marking Controller classes/methods as requiring userId
 * - UserContext: ThreadLocal wrapper for current userId, set by interceptor and cleared after request
 * - AuthInterceptor: Spring MVC interceptor extracting X-User-Id header into UserContext
 * - AuthAutoConfiguration: Spring Boot auto-configuration registering the interceptor
 */
package com.octopus.demo.common.auth;
