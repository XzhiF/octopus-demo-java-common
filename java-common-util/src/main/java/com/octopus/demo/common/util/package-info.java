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
 * - MaskUtils: sensitive-data masking for phones, emails and free text (phone/email/id card/bank card/name/address)
 * - MaskType: categories recognized by {@link com.octopus.demo.common.util.MaskUtils#maskText(String)}
 * - JwtTokenExpiredException: exception thrown when JWT token is expired
 * - JwtTokenInvalidException: exception thrown when JWT token is invalid
 */
package com.octopus.demo.common.util;