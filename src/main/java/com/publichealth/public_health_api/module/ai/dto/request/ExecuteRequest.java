package com.publichealth.public_health_api.module.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 意图执行请求 DTO
 */
@Data
public class ExecuteRequest {

    /**
     * 会话 ID（可选）
     */
    private String sessionId;

    /**
     * 意图类型
     */
    @NotBlank(message = "意图类型不能为空")
    private String intent;

    /**
     * 实体类型
     */
    private String entity;

    /**
     * 执行参数
     */
    private Map<String, Object> params;
}
