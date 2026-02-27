package com.publichealth.public_health_api.module.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 创建时间戳
     */
    private Long timestamp;
}
