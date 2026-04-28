package com.publichealth.public_health_api.module.reportcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 传染病报告卡审核记录实体类
 * 对应数据库表: report_card_audit
 */
@Entity
@Table(name = "report_card_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardAudit {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "report_card_id", nullable = false, length = 36)
    private String reportCardId;

    @Column(name = "auditor_id", length = 36)
    private String auditorId;

    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", length = 20)
    private AuditStatus auditStatus;

    @Column(name = "audit_date")
    private LocalDateTime auditDate;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "assign_status", length = 20)
    private AssignStatus assignStatus;

    @Column(name = "assignee_id", length = 36)
    private String assigneeId;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.auditStatus == null) {
            this.auditStatus = AuditStatus.PENDING;
        }
        if (this.assignStatus == null) {
            this.assignStatus = AssignStatus.UNASSIGNED;
        }
    }

    public enum AuditStatus {
        PENDING("待审核"),
        APPROVED("已审核"),
        REJECTED("审核不通过");

        private final String description;

        AuditStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AssignStatus {
        UNASSIGNED("未分配"),
        ASSIGNED("已分配"),
        IN_PROGRESS("处理中"),
        COMPLETED("已完成"),
        VOID("已作废");

        private final String description;

        AssignStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
