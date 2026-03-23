package com.publichealth.public_health_api.module.ai.dto.response;

import com.publichealth.public_health_api.module.ai.entity.AiSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI会话 DTO
 * 用于从 AiSession 实体转换为响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

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
     * 更新时间（时间戳）
     */
    private Long updatedAt;

    /**
     * 从 AiSession 实体转换为 DTO
     *
     * @param entity AiSession 实体
     * @return SessionDTO
     */
    public static SessionDTO fromEntity(AiSession entity) {
        if (entity == null) {
            return null;
        }

        return SessionDTO.builder()
                .sessionId(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .messageCount(entity.getMessageCount())
                .lastMessageAt(toTimestamp(entity.getLastMessageAt()))
                .createdAt(toTimestamp(entity.getCreatedAt()))
                .updatedAt(toTimestamp(entity.getUpdatedAt()))
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
