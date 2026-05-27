package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.BaseException;

/**
 * Exception thrown when a JWT token is invalid (bad signature, malformed, etc).
 */
public class JwtTokenInvalidException extends BaseException {

    public JwtTokenInvalidException(String message) {
        super(401, message);
    }

    public JwtTokenInvalidException(String message, Throwable cause) {
        super(401, message, cause);
    }
}