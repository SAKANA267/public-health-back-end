package com.publichealth.public_health_api.module.ai.dto;

import java.util.Map;

/**
 * 结构化聊天响应 - Spring AI 实体提取
 * LLM 会返回符合此结构的 JSON
 */
public record StructuredChatResponse(
        String message,           // AI 回复消息
        ActionInfo action         // 操作信息
) {
    /**
     * 操作信息
     */
    public record ActionInfo(
            String type,                      // NAVIGATE, API, CALLBACK 或 null
            Map<String, Object> payload       // 操作参数
    ) {}
}
