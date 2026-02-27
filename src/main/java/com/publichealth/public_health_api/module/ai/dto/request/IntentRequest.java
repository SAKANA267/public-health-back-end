package com.publichealth.public_health_api.module.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 意图识别请求 DTO
 */
@Data
public class IntentRequest {

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
