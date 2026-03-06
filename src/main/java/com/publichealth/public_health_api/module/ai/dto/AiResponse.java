package com.publichealth.public_health_api.module.ai.dto;

import java.util.List;

/**
 * AI 聊天响应 - Spring AI 结构化输出
 */
public record AiResponse(
        String message,           // AI 回复消息
        Action action,            // 建议执行的操作
        List<String> suggestions, // 建议操作列表
        String sessionId          // 会话ID
) {
    /**
     * 操作指令
     */
    public record Action(
            String type,                 // NAVIGATE, API, CALLBACK
            MapWrapper payload           // 操作参数
    ) {}

    /**
     * Map 包装类，用于序列化
     */
    public record MapWrapper(java.util.Map<String, Object> data) {
        public MapWrapper() {
            this(new java.util.HashMap<>());
        }
    }
}
