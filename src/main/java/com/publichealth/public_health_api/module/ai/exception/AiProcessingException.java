package com.publichealth.public_health_api.module.ai.exception;

/**
 * AI 处理异常
 * 当 AI 请求处理失败时抛出（如解析错误、格式错误等）
 */
public class AiProcessingException extends AiServiceException {

    public AiProcessingException(String message) {
        super("AI_PROCESSING_ERROR", message);
    }

    public AiProcessingException(String message, Throwable cause) {
        super("AI_PROCESSING_ERROR", message, cause);
    }
}
