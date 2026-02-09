package com.publichealth.public_health_api.module.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 刷新令牌实体
 * 对应数据库表: refresh_token
 */
@Entity
@Table(name = "refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

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

    /**
     * 令牌字符串
     */
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    // ============================================
    // 关联字段
    // ============================================

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    // ============================================
    // 时间戳字段
    // ============================================

    /**
     * 过期时间
     */
    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    /**
     * 创建时间 (自动填充)
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间 (自动更新)
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // ============================================
    // 逻辑删除标记 (必须字段)
    // ============================================

    /**
     * 逻辑删除标记 (false-未删除, true-已删除)
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // ============================================
    // 生命周期回调
    // ============================================

    /**
     * 生命周期回调: 持久化前生成UUID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
    }

    // ============================================
    // 业务方法
    // ============================================

    /**
     * 检查令牌是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryTime);
    }
}
