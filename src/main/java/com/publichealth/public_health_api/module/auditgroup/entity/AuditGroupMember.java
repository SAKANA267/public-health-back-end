package com.publichealth.public_health_api.module.auditgroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 审核组成员关联实体类
 * 对应数据库表: audit_group_member
 */
@Entity
@Table(name = "audit_group_member",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "user_id"}, name = "uk_group_user")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditGroupMember {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 审核组ID (外键)
     */
    @Column(name = "group_id", nullable = false, length = 36)
    private String groupId;

    /**
     * 用户ID (外键)
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * 加入时间
     */
    @CreationTimestamp
    @Column(name = "join_time", nullable = false, updatable = false)
    private LocalDateTime joinTime;

    /**
     * 创建时间 (自动填充)
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 生命周期回调: 持久化前生成UUID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}
