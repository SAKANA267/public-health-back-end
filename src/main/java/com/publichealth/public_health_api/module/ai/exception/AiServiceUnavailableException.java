package com.publichealth.public_health_api.module.ai.exception;

/**
 * AI 服务不可用异常
 * 当 AI 服务因网络问题、配额限制等原因无法访问时抛出
 */
public class AiServiceUnavailableException extends AiServiceException {

    public AiServiceUnavailableException(String message) {
        super("AI_SERVICE_UNAVAILABLE", message);
    }

    public AiServiceUnavailableException(String message, Throwable cause) {
        super("AI_SERVICE_UNAVAILABLE", message, cause);
    }
}
