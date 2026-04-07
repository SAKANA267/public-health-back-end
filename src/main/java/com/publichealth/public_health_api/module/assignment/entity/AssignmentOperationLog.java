package com.publichealth.public_health_api.module.assignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 任务操作日志实体类
 * 对应数据库表: assignment_operation_log
 */
@Entity
@Table(name = "assignment_operation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentOperationLog {

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
     * 任务ID
     */
    @Column(name = "assignment_id", nullable = false, length = 36)
    private String assignmentId;

    /**
     * 报卡ID
     */
    @Column(name = "report_card_id", nullable = false, length = 36)
    private String reportCardId;

    // ============================================
    // 操作信息
    // ============================================

    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 30)
    private OperationType operationType;

    /**
     * 操作人ID
     */
    @Column(name = "operator_id", nullable = false, length = 36)
    private String operatorId;

    /**
     * 操作人姓名
     */
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    // ============================================
    // 状态变更
    // ============================================

    /**
     * 操作前状态
     */
    @Column(name = "before_status", length = 20)
    private String beforeStatus;

    /**
     * 操作后状态
     */
    @Column(name = "after_status", length = 20)
    private String afterStatus;

    // ============================================
    // 操作详情
    // ============================================

    /**
     * 操作详情JSON
     */
    @Column(name = "operation_detail", columnDefinition = "TEXT")
    private String operationDetail;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    // ============================================
    // 标准字段
    // ============================================

    /**
     * 操作时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // ============================================
    // 生命周期回调
    // ============================================

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    // ============================================
    // 枚举定义
    // ============================================

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        ASSIGN("分配"),
        ACCEPT("接单"),
        COMPLETE("完成"),
        CANCEL("取消"),
        REASSIGN("重新分配");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
