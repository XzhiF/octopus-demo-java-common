package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.BaseException;

/**
 * Exception thrown when a JWT token has expired.
 */
public class JwtTokenExpiredException extends BaseException {

    public JwtTokenExpiredException() {
        super(401, "JWT token expired");
    }

    public JwtTokenExpiredException(Throwable cause) {
        super(401, "JWT token expired", cause);
    }
}
