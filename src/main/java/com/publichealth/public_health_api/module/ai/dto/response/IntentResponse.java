package com.publichealth.public_health_api.module.ai.dto.response;

import com.publichealth.public_health_api.module.ai.enums.EntityType;
import com.publichealth.public_health_api.module.ai.enums.IntentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 意图识别响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResponse {

    /**
     * 意图类型
     */
    private IntentType intent;

    /**
     * 实体类型
     */
    private EntityType entity;

    /**
     * 提取的参数
     */
    private Map<String, Object> params;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;
}
