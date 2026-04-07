package com.publichealth.public_health_api.module.assignment.entity;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 审核组工作统计实体类
 * 对应数据库表: audit_group_work_stats
 */
@Entity
@Table(name = "audit_group_work_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditGroupWorkStats {

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
     * 审核组ID (唯一)
     */
    @Column(name = "audit_group_id", nullable = false, unique = true, length = 36)
    private String auditGroupId;

    // ============================================
    // 任务统计
    // ============================================

    /**
     * 累计分配任务数
     */
    @Column(name = "total_assigned", nullable = false)
    private Integer totalAssigned = 0;

    /**
     * 累计完成任务数
     */
    @Column(name = "total_completed", nullable = false)
    private Integer totalCompleted = 0;

    /**
     * 累计取消任务数
     */
    @Column(name = "total_cancelled", nullable = false)
    private Integer totalCancelled = 0;

    // ============================================
    // 当前任务
    // ============================================

    /**
     * 当前待处理任务数
     */
    @Column(name = "pending_count", nullable = false)
    private Integer pendingCount = 0;

    /**
     * 当前处理中任务数
     */
    @Column(name = "in_progress_count", nullable = false)
    private Integer inProgressCount = 0;

    // ============================================
    // 效率统计
    // ============================================

    /**
     * 平均处理时长(分钟)
     */
    @Column(name = "avg_process_time")
    private Integer avgProcessTime;

    /**
     * 最后任务时间
     */
    @Column(name = "last_task_time")
    private LocalDateTime lastTaskTime;

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
     * 审核组关联
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AuditGroup auditGroup;

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
        if (this.totalAssigned == null) {
            this.totalAssigned = 0;
        }
        if (this.totalCompleted == null) {
            this.totalCompleted = 0;
        }
        if (this.totalCancelled == null) {
            this.totalCancelled = 0;
        }
        if (this.pendingCount == null) {
            this.pendingCount = 0;
        }
        if (this.inProgressCount == null) {
            this.inProgressCount = 0;
        }
    }

    // ============================================
    // 业务方法
    // ============================================

    /**
     * 增加分配任务数
     */
    public void incrementAssigned() {
        this.totalAssigned++;
        this.pendingCount++;
        this.lastTaskTime = LocalDateTime.now();
    }

    /**
     * 任务开始处理
     */
    public void startTask() {
        this.pendingCount--;
        this.inProgressCount++;
    }

    /**
     * 任务完成
     */
    public void completeTask() {
        this.inProgressCount--;
        this.totalCompleted++;
        this.lastTaskTime = LocalDateTime.now();
    }

    /**
     * 任务取消
     */
    public void cancelTask() {
        if (this.pendingCount > 0) {
            this.pendingCount--;
        } else if (this.inProgressCount > 0) {
            this.inProgressCount--;
        }
        this.totalCancelled++;
    }

    /**
     * 计算当前任务总数
     */
    public Integer getCurrentTaskCount() {
        return this.pendingCount + this.inProgressCount;
    }
}
