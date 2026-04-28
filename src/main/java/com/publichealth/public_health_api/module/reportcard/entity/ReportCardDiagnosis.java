package com.publichealth.public_health_api.module.reportcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 传染病报告卡诊断信息实体类
 * 对应数据库表: report_card_diagnosis
 */
@Entity
@Table(name = "report_card_diagnosis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardDiagnosis {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "report_card_id", nullable = false, length = 36, unique = true)
    private String reportCardId;

    @Column(name = "disease_name", nullable = false, length = 100)
    private String diseaseName;

    @Column(name = "diagnosis_code", length = 50)
    private String diagnosisCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_belong", length = 20)
    private PatientBelong patientBelong;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "crowd_categories", columnDefinition = "json")
    private List<String> crowdCategories;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", length = 20)
    private CaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_attribute", length = 20)
    private CaseAttribute caseAttribute;

    @Column(name = "onset_date")
    private LocalDate onsetDate;

    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosisDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

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
        if (this.patientBelong == null) {
            this.patientBelong = PatientBelong.LOCAL;
        }
        if (this.caseType == null) {
            this.caseType = CaseType.SUSPECTED;
        }
        if (this.caseAttribute == null) {
            this.caseAttribute = CaseAttribute.ACUTE;
        }
    }

    public enum PatientBelong {
        LOCAL("本地"),
        NON_LOCAL("外来");

        private final String description;

        PatientBelong(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum CaseType {
        SUSPECTED("疑似"),
        CLINICAL("临床"),
        CONFIRMED("确诊"),
        PATHOGEN("病原");

        private final String description;

        CaseType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum CaseAttribute {
        ACUTE("急性"),
        CHRONIC("慢性");

        private final String description;

        CaseAttribute(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
