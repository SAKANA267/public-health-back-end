package com.publichealth.public_health_api.module.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AI 会话实体
 * 管理用户与 AI 助手的对话会话
 */
@Data
@Entity
@Table(name = "ai_session")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSession {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private String id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * 会话标题（首条消息摘要）
     */
    @Column(length = 200)
    private String title;

    /**
     * 消息数量
     */
    @Column(name = "message_count")
    private Integer messageCount = 0;

    /**
     * 最后消息时间
     */
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
