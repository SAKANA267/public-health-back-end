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
 * 任务分配规则实体类
 * 对应数据库表: assignment_rule
 */
@Entity
@Table(name = "assignment_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRule {

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
    // 规则基本信息
    // ============================================

    /**
     * 规则名称
     */
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    /**
     * 规则编码 (唯一)
     */
    @Column(name = "rule_code", nullable = false, unique = true, length = 50)
    private String ruleCode;

    // ============================================
    // 规则条件
    // ============================================

    /**
     * 适用病种分类
     */
    @Column(name = "disease_category", length = 50)
    private String diseaseCategory;

    /**
     * 适用院区
     */
    @Column(name = "hospital_area", length = 50)
    private String hospitalArea;

    /**
     * 适用科室
     */
    @Column(name = "department", length = 50)
    private String department;

    // ============================================
    // 分配策略
    // ============================================

    /**
     * 分配策略
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "assign_strategy", nullable = false, length = 30)
    private AssignStrategy assignStrategy = AssignStrategy.ROUND_ROBIN;

    /**
     * 指定目标审核组ID (当策略为MANUAL时使用)
     */
    @Column(name = "target_group_id", length = 36)
    private String targetGroupId;

    // ============================================
    // 优先级配置
    // ============================================

    /**
     * 默认优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private ReportCardAssignment.AssignmentPriority priority = ReportCardAssignment.AssignmentPriority.NORMAL;

    /**
     * 默认截止时长(小时)
     */
    @Column(name = "deadline_hours")
    private Integer deadlineHours;

    // ============================================
    // 规则状态
    // ============================================

    /**
     * 规则状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RuleStatus status = RuleStatus.ACTIVE;

    /**
     * 规则优先级顺序 (数字越小优先级越高)
     */
    @Column(name = "rule_order", nullable = false)
    private Integer ruleOrder = 0;

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
     * 目标审核组关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AuditGroup targetAuditGroup;

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
            this.status = RuleStatus.ACTIVE;
        }
        if (this.ruleOrder == null) {
            this.ruleOrder = 0;
        }
        if (this.assignStrategy == null) {
            this.assignStrategy = AssignStrategy.ROUND_ROBIN;
        }
        if (this.priority == null) {
            this.priority = ReportCardAssignment.AssignmentPriority.NORMAL;
        }
    }

    // ============================================
    // 枚举定义
    // ============================================

    /**
     * 分配策略枚举
     */
    public enum AssignStrategy {
        ROUND_ROBIN("轮询分配"),
        LEAST_TASKS("最少任务优先"),
        MANUAL("手动指定"),
        LEADER("组长分配");

        private final String description;

        AssignStrategy(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 规则状态枚举
     */
    public enum RuleStatus {
        ACTIVE("启用"),
        INACTIVE("停用");

        private final String description;

        RuleStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
