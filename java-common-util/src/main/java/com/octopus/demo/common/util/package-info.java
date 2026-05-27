/**
 * Utility module providing common utility classes.
 *
 * Includes:
 * - JwtUtil: JWT generation and parsing using HMAC-SHA256 (static utility with configurable defaults)
 * - JwtConfig: configuration record for JwtUtil settings
 * - AEDUtils: AES encrypt/decrypt utility using CBC/PKCS5Padding (static utility with configurable defaults)
 * - AEDConfig: configuration record for AEDUtils settings
 * - MD5Utils: MD5 hash digest utility (not for password storage)
 * - HighlightingUtils: keyword highlighting for search results
 * - JwtTokenExpiredException: exception thrown when JWT token is expired
 * - JwtTokenInvalidException: exception thrown when JWT token is invalid
 */
package com.octopus.demo.common.util;