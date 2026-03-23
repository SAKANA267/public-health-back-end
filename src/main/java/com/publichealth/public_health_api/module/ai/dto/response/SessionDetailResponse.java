package com.publichealth.public_health_api.module.ai.dto.response;

import com.publichealth.public_health_api.module.ai.entity.AiChatMessage;
import com.publichealth.public_health_api.module.ai.entity.AiSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话详情响应 DTO
 * 包含会话信息和完整的历史消息列表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailResponse {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 最后消息时间（时间戳）
     */
    private Long lastMessageAt;

    /**
     * 创建时间（时间戳）
     */
    private Long createdAt;

    /**
     * 消息列表
     */
    private List<MessageInfo> messages;

    /**
     * 从 AiSession 实体和消息列表转换为 DTO
     *
     * @param session  AiSession 实体
     * @param messages 消息列表
     * @return SessionDetailResponse
     */
    public static SessionDetailResponse fromEntity(AiSession session, List<AiChatMessage> messages) {
        if (session == null) {
            return null;
        }

        List<MessageInfo> messageInfos = messages.stream()
                .map(MessageInfo::fromEntity)
                .collect(Collectors.toList());

        return SessionDetailResponse.builder()
                .sessionId(session.getId())
                .userId(session.getUserId())
                .title(session.getTitle())
                .messageCount(session.getMessageCount())
                .lastMessageAt(toTimestamp(session.getLastMessageAt()))
                .createdAt(toTimestamp(session.getCreatedAt()))
                .messages(messageInfos)
                .build();
    }

    /**
     * 将 LocalDateTime 转换为时间戳（毫秒）
     *
     * @param dateTime LocalDateTime
     * @return 时间戳
     */
    private static Long toTimestamp(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 消息信息
     * 用于在会话详情中展示单条消息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageInfo {
        /**
         * 消息 ID
         */
        private String id;

        /**
         * 角色
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
         */
        private String messageType;

        /**
         * 时间戳
         */
        private Long timestamp;

        /**
         * 从 AiChatMessage 实体转换为 MessageInfo
         *
         * @param entity AiChatMessage 实体
         * @return MessageInfo
         */
        public static MessageInfo fromEntity(AiChatMessage entity) {
            if (entity == null) {
                return null;
            }

            return MessageInfo.builder()
                    .id(entity.getId())
                    .role(entity.getRole())
                    .content(entity.getContent())
                    .messageType(entity.getMessageType())
                    .timestamp(toTimestamp(entity.getCreatedAt()))
                    .build();
        }

        /**
         * 将 LocalDateTime 转换为时间戳（毫秒）
         *
         * @param dateTime LocalDateTime
         * @return 时间戳
         */
        private static Long toTimestamp(java.time.LocalDateTime dateTime) {
            if (dateTime == null) {
                return null;
            }
            return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }
}
