package com.octopus.demo.common.bean;

/**
 * Unified API response wrapper.
 * Immutable — created via static factory methods only.
 */
public class R<T> {

    private final int code;
    private final T data;
    private final String msg;
    private final long timestamp;

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return new R<>(200, null, "success");
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, data, "success");
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(500, null, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, null, msg);
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public long getTimestamp() {
        return timestamp;
    }
}