package com.publichealth.public_health_api.module.ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AI聊天消息实体类
 * 对应数据库表: ai_chat_message
 */
@Entity
@Table(name = "ai_chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage {

    // ============================================
    // 主键字段
    // ============================================

    /**
     * 主键ID
     * 使用UUID作为主键
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    // ============================================
    // 会话关联字段
    // ============================================

    /**
     * 会话ID
     * 关联到 ai_session.id
     */
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    // ============================================
    // 消息内容字段
    // ============================================

    /**
     * 消息角色
     * user - 用户消息
     * assistant - AI助手消息
     * system - 系统消息
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型
     * text - 普通文本消息
     * action - 带结构化输出的消息
     * error - 错误消息
     */
    @Column(name = "message_type", length = 20)
    private String messageType = "text";

    /**
     * 元数据
     * 存储结构化输出、建议等信息
     * JSON格式字符串
     */
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // ============================================
    // 时间戳字段
    // ============================================

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // 关联关系
    // ============================================

    /**
     * 关联的会话
     * 多对一关联，懒加载
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AiSession session;

    // ============================================
    // 生命周期回调
    // ============================================

    /**
     * 生命周期回调: 持久化前生成UUID和设置默认值
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.messageType == null) {
            this.messageType = "text";
        }
    }

    // ============================================
    // 业务方法
    // ============================================

    /**
     * 判断是否为用户消息
     */
    public boolean isUserMessage() {
        return "user".equalsIgnoreCase(this.role);
    }

    /**
     * 判断是否为助手消息
     */
    public boolean isAssistantMessage() {
        return "assistant".equalsIgnoreCase(this.role);
    }

    /**
     * 判断是否为系统消息
     */
    public boolean isSystemMessage() {
        return "system".equalsIgnoreCase(this.role);
    }

    /**
     * 判断是否为带结构化输出的消息
     */
    public boolean isActionMessage() {
        return "action".equalsIgnoreCase(this.messageType);
    }

    /**
     * 判断是否为错误消息
     */
    public boolean isErrorMessage() {
        return "error".equalsIgnoreCase(this.messageType);
    }
}
