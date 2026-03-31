package com.publichealth.public_health_api.module.auditgroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 审核组实体类
 * 对应数据库表: audit_group
 */
@Entity
@Table(name = "audit_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditGroup {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 审核组名称 (唯一)
     */
    @Column(name = "group_name", nullable = false, unique = true, length = 50)
    private String groupName;

    /**
     * 审核组编码 (唯一)
     */
    @Column(name = "group_code", nullable = false, unique = true, length = 20)
    private String groupCode;

    /**
     * 审核组描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 组长用户ID
     */
    @Column(name = "leader_id", length = 36)
    private String leaderId;

    /**
     * 组状态: active, inactive
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditGroupStatus status = AuditGroupStatus.ACTIVE;

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
     * 审核组状态枚举
     */
    public enum AuditGroupStatus {
        ACTIVE,       // 启用
        INACTIVE      // 停用
    }
}
