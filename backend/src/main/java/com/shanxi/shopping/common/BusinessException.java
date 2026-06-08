package com.shanxi.shopping.common;

/**
 * 业务异常 — 由全局异常处理器捕获并转换为 Result
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    public int getCode() {
        return code;
    }
}
