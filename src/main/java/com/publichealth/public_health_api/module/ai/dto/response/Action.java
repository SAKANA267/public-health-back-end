package com.publichealth.public_health_api.module.ai.dto.response;

import com.publichealth.public_health_api.module.ai.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 操作指令 DTO
 * 告诉前端执行什么操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    /**
     * 操作类型
     */
    private ActionType type;

    /**
     * 操作参数
     */
    private Map<String, Object> payload;
}
