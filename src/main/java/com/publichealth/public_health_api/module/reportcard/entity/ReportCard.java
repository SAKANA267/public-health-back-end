package com.publichealth.public_health_api.module.reportcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 传染病报告卡主表实体类
 * 对应数据库表: report_card
 *
 * 迁移说明：原单表已拆分为4张关联表
 * - report_card: 主表（核心信息 + 冗余字段）
 * - report_card_patient: 患者信息表
 * - report_card_diagnosis: 诊断信息表
 * - report_card_audit: 审核记录表
 */
@Entity
@Table(name = "report_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCard {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "card_number", length = 50, unique = true)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_category", length = 20)
    private ReportCategory reportCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", length = 20)
    private ReportStatus reportStatus;

    @Column(name = "hospital_area", nullable = false, length = 50)
    private String hospitalArea;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "inpatient_no", length = 20)
    private String inpatientNo;

    @Column(name = "outpatient_no", length = 20)
    private String outpatientNo;

    @Column(name = "doctor_name", nullable = false, length = 50)
    private String doctorName;

    @Column(name = "fill_date", nullable = false)
    private LocalDate fillDate;

    @Column(name = "patient_name", nullable = false, length = 50)
    private String patientName;

    @Column(name = "disease_name", nullable = false, length = 100)
    private String diseaseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", length = 20)
    private AuditStatus auditStatus;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.auditStatus == null) {
            this.auditStatus = AuditStatus.PENDING;
        }
        if (this.reportCategory == null) {
            this.reportCategory = ReportCategory.INITIAL;
        }
        if (this.reportStatus == null) {
            this.reportStatus = ReportStatus.UNREPORTED;
        }
    }

    public enum ReportCategory {
        INITIAL("初次报告"),
        CORRECTION("订正报告");

        private final String description;

        ReportCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ReportStatus {
        REPORTED("已上报"),
        UNREPORTED("未上报");

        private final String description;

        ReportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
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
}
