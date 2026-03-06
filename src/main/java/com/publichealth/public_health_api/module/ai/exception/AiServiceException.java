package com.publichealth.public_health_api.module.ai.exception;

/**
 * AI 服务异常基类
 */
public class AiServiceException extends RuntimeException {

    private final String errorCode;

    public AiServiceException(String message) {
        super(message);
        this.errorCode = "AI_SERVICE_ERROR";
    }

    public AiServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AI_SERVICE_ERROR";
    }

    public AiServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
