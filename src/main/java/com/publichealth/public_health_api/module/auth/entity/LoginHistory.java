package com.publichealth.public_health_api.module.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 登录历史实体
 * 对应数据库表: login_history
 */
@Entity
@Table(name = "login_history", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_login_time", columnList = "login_time"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistory {

    // ============================================
    // 枚举定义
    // ============================================

    /**
     * 登录状态枚举
     */
    public enum LoginStatus {
        /**
         * 登录成功
         */
        SUCCESS,
        /**
         * 登录失败
         */
        FAILURE
    }

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
     * 关联 sys_user 表
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * 用户名
     * 冗余存储，方便查询展示
     */
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    // ============================================
    // 登录信息字段
    // ============================================

    /**
     * 登录时间
     */
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    /**
     * 登录地点
     * 可选，根据IP解析获取
     */
    @Column(name = "login_location", length = 100)
    private String loginLocation;

    /**
     * IP地址
     * 客户端请求IP
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 浏览器信息
     * User-Agent请求头
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 登录状态
     * SUCCESS-成功, FAILURE-失败
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoginStatus status;

    /**
     * 失败原因
     * 登录失败时记录具体原因
     */
    @Column(name = "fail_reason", length = 200)
    private String failReason;

    // ============================================
    // 时间戳字段
    // ============================================

    /**
     * 创建时间 (自动填充)
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // ============================================
    // 生命周期回调
    // ============================================

    /**
     * 生命周期回调: 持久化前生成UUID和设置默认值
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.loginTime == null) {
            this.loginTime = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = LoginStatus.SUCCESS;
        }
    }
}
