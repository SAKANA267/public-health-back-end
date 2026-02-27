package com.publichealth.public_health_api.module.ai.dto.request;

import lombok.Data;

/**
 * 创建会话请求 DTO
 */
@Data
public class CreateSessionRequest {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 会话标题
     */
    private String title;
}
