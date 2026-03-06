package com.publichealth.public_health_api.module.ai.exception;

import com.publichealth.public_health_api.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AI 模块全局异常处理器
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.publichealth.public_health_api.module.ai")
public class AiExceptionHandler {

    /**
     * 处理 AI 服务不可用异常
     */
    @ExceptionHandler(AiServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiServiceUnavailable(AiServiceUnavailableException e) {
        log.error("AI 服务不可用: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(503, "AI 服务暂时不可用，请稍后重试"));
    }

    /**
     * 处理 AI 处理异常
     */
    @ExceptionHandler(AiProcessingException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiProcessing(AiProcessingException e) {
        log.error("AI 处理失败: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "处理请求时发生错误"));
    }

    /**
     * 处理通用 AI 服务异常
     */
    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiService(AiServiceException e) {
        log.error("AI 服务异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, e.getMessage()));
    }
}
