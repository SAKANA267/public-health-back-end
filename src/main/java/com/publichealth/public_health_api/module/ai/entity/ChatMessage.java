package com.publichealth.public_health_api.module.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天消息实体
 * 存储 AI 会话中的对话消息
 */
@Data
@Entity
@Table(name = "ai_chat_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private String id;

    /**
     * 会话 ID
     */
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    /**
     * 角色: user, assistant, system
     */
    @Column(nullable = false, length = 20)
    private String role;

    /**
     * 消息内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型: text, action, error
     */
    @Column(name = "message_type", length = 20)
    private String messageType = "text";

    /**
     * 元数据（包含 action、suggestions 等）
     */
    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }
}
