package com.publichealth.public_health_api.module.sysuser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 对应数据库表: sys_user
 */
@Entity
@Table(name = "sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    /**
     * 主键ID
     * 使用UUID作为主键
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 用户名 (唯一)
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码 (加密存储)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 电子邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 角色: super_admin, admin, auditor, user, guest
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /**
     * 状态: active, inactive
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 数据范围 (用于数据权限控制)
     */
    @Column(name = "data_scope", length = 20)
    private String dataScope;

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

    /**
     * 最后登录时间
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * 逻辑删除标记 (0-未删除, 1-已删除)
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

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

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        SUPER_ADMIN,  // 超级管理员
        ADMIN,        // 管理员
        AUDITOR,      // 审计员
        USER,         // 普通用户
        GUEST         // 访客
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,       // 激活
        INACTIVE      // 停用
    }
}
