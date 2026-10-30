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
 * - PhraseMatchUtils: ES-style phrase matching with slop and relevance score
 * - PhraseMatchResult: result record (matched / score / actualSlop / span)
 * - Tokenizer: pluggable tokenizer contract (default: SimpleTokenizer)
 * - JwtTokenExpiredException: exception thrown when JWT token is expired
 * - JwtTokenInvalidException: exception thrown when JWT token is invalid
 */
package com.octopus.demo.common.util;