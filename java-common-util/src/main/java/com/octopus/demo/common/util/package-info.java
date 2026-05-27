/**
 * Utility module providing JWT generation and parsing.
 *
 * Includes:
 * - JwtUtil: core JWT generation and parsing utility using HMAC-SHA256
 * - JwtProperties: Spring Boot configuration properties for JWT settings
 * - JwtAutoConfiguration: Spring Boot auto-configuration for JwtUtil bean
 * - JwtTokenExpiredException: exception thrown when JWT token is expired
 * - JwtTokenInvalidException: exception thrown when JWT token is invalid
 */
package com.octopus.demo.common.util;