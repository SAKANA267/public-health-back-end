package com.publichealth.public_health_api.module.ai.dto.response;

import com.publichealth.public_health_api.module.ai.entity.AiChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI聊天消息 DTO
 * 用于从 AiChatMessage 实体转换为响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    /**
     * 消息 ID
     */
    private String messageId;

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 消息角色
     * user - 用户消息
     * assistant - AI助手消息
     * system - 系统消息
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     * text - 普通文本消息
     * action - 带结构化输出的消息
     * error - 错误消息
     */
    private String messageType;

    /**
     * 元数据
     * JSON格式字符串，存储结构化输出等信息
     */
    private String metadata;

    /**
     * 创建时间（时间戳）
     */
    private Long createdAt;

    /**
     * 从 AiChatMessage 实体转换为 DTO
     *
     * @param entity AiChatMessage 实体
     * @return MessageDTO
     */
    public static MessageDTO fromEntity(AiChatMessage entity) {
        if (entity == null) {
            return null;
        }

        return MessageDTO.builder()
                .messageId(entity.getId())
                .sessionId(entity.getSessionId())
                .role(entity.getRole())
                .content(entity.getContent())
                .messageType(entity.getMessageType())
                .metadata(entity.getMetadata())
                .createdAt(toTimestamp(entity.getCreatedAt()))
                .build();
    }

    /**
     * 将 LocalDateTime 转换为时间戳（毫秒）
     *
     * @param dateTime LocalDateTime
     * @return 时间戳
     */
    private static Long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
