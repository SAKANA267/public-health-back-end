package com.publichealth.public_health_api.module.assignment.entity;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 报卡分配记录实体类
 * 对应数据库表: report_card_assignment
 */
@Entity
@Table(name = "report_card_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardAssignment {

    // ============================================
    // 主键字段
    // ============================================

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    // ============================================
    // 关联字段
    // ============================================

    /**
     * 报卡ID (外键)
     */
    @Column(name = "report_card_id", nullable = false, length = 36)
    private String reportCardId;

    /**
     * 审核组ID (外键)
     */
    @Column(name = "audit_group_id", nullable = false, length = 36)
    private String auditGroupId;

    /**
     * 分配人ID (外键)
     */
    @Column(name = "assigner_id", nullable = false, length = 36)
    private String assignerId;

    // ============================================
    // 任务状态控制
    // ============================================

    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    // ============================================
    // 时间控制
    // ============================================

    /**
     * 分配时间
     */
    @CreationTimestamp
    @Column(name = "assign_time", nullable = false, updatable = false)
    private LocalDateTime assignTime;

    /**
     * 截止时间
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;

    /**
     * 审核组接单时间
     */
    @Column(name = "accept_time")
    private LocalDateTime acceptTime;

    /**
     * 完成时间
     */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    // ============================================
    // 优先级
    // ============================================

    /**
     * 优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private AssignmentPriority priority = AssignmentPriority.NORMAL;

    // ============================================
    // 备注信息
    // ============================================

    /**
     * 分配备注
     */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    /**
     * 取消/退回原因
     */
    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    // ============================================
    // 乐观锁
    // ============================================

    /**
     * 乐观锁版本号
     */
    @Version
    @Column(name = "version")
    private Integer version = 0;

    // ============================================
    // 标准字段
    // ============================================

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // ============================================
    // 关联关系 (只读)
    // ============================================

    /**
     * 报卡关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_card_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ReportCard reportCard;

    /**
     * 审核组关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AuditGroup auditGroup;

    /**
     * 分配人关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigner_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SysUser assigner;

    // ============================================
    // 生命周期回调
    // ============================================

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.status == null) {
            this.status = AssignmentStatus.PENDING;
        }
        if (this.priority == null) {
            this.priority = AssignmentPriority.NORMAL;
        }
        if (this.version == null) {
            this.version = 0;
        }
    }

    // ============================================
    // 枚举定义
    // ============================================

    /**
     * 任务状态枚举
     */
    public enum AssignmentStatus {
        PENDING("待处理"),
        IN_PROGRESS("处理中"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        AssignmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 优先级枚举
     */
    public enum AssignmentPriority {
        LOW("低"),
        NORMAL("普通"),
        HIGH("高"),
        URGENT("紧急");

        private final String description;

        AssignmentPriority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
