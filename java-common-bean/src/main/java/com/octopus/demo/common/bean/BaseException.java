package com.octopus.demo.common.bean;

/**
 * Business exception base class with error code aligned with R.code semantics.
 */
public class BaseException extends RuntimeException {

    private final int code;

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BaseException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}