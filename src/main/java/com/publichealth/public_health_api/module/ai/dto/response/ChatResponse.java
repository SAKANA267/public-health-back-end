package com.publichealth.public_health_api.module.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * AI 回复消息
     */
    private String message;

    /**
     * 需要执行的操作
     */
    private Action action;

    /**
     * 建议操作
     */
    private List<String> suggestions;
}
