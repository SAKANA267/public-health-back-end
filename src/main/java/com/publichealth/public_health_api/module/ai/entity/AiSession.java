package com.publichealth.public_health_api.module.ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI会话实体类
 * 对应数据库表: ai_session
 */
@Entity
@Table(name = "ai_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSession {

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
    // 用户关联字段
    // ============================================

    /**
     * 用户ID
     * 用于多用户数据隔离
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    // ============================================
    // 会话信息字段
    // ============================================

    /**
     * 会话标题
     * 通常取首条消息的前20个字符
     */
    @Column(name = "title", length = 200)
    private String title;

    /**
     * 消息数量
     * 会话中的消息总数
     */
    @Column(name = "message_count")
    private Integer messageCount = 0;

    /**
     * 最后消息时间
     * 用于会话列表排序
     */
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    // ============================================
    // 时间戳字段
    // ============================================

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================
    // 逻辑删除标记
    // ============================================

    /**
     * 逻辑删除标记
     * false-未删除, true-已删除
     */
    @Column(name = "deleted")
    private Boolean deleted = false;

    // ============================================
    // 关联关系
    // ============================================

    /**
     * 会话的消息列表
     * 一对多关联
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiChatMessage> messages = new ArrayList<>();

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
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.messageCount == null) {
            this.messageCount = 0;
        }
    }

    /**
     * 生命周期回调: 更新前设置更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ============================================
    // 业务方法
    // ============================================

    /**
     * 增加消息计数
     */
    public void incrementMessageCount() {
        this.messageCount = (this.messageCount == null ? 0 : this.messageCount) + 1;
    }

    /**
     * 更新最后消息时间
     */
    public void updateLastMessageAt() {
        this.lastMessageAt = LocalDateTime.now();
    }

    /**
     * 软删除
     */
    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 判断是否已删除
     */
    public boolean isDeleted() {
        return this.deleted != null && this.deleted;
    }
}
