package com.publichealth.public_health_api.exception;

/**
 * 业务异常类
 * 用于表示业务逻辑中的错误情况
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
