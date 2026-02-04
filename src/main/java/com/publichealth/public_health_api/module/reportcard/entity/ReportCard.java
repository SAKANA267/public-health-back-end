package com.publichealth.public_health_api.module.reportcard.entity;

import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 传染病报告卡实体类
 * 对应数据库表: report_card
 */
@Entity
@Table(name = "report_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCard {

    // ============================================
    // 主键字段
    // ============================================

    /**
     * 主键ID
     * 使用UUID作为主键
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    // ============================================
    // 基础信息字段
    // ============================================

    /**
     * 院区
     */
    @Column(name = "hospital_area", nullable = false, length = 50)
    private String hospitalArea;

    /**
     * 科室
     */
    @Column(name = "department", nullable = false, length = 50)
    private String department;

    /**
     * 诊断名称
     */
    @Column(name = "diagnosis_name", nullable = false, length = 100)
    private String diagnosisName;

    // ============================================
    // 患者信息字段
    // ============================================

    /**
     * 住院号
     */
    @Column(name = "inpatient_no", nullable = false, length = 20)
    private String inpatientNo;

    /**
     * 门诊号
     */
    @Column(name = "outpatient_no", nullable = false, length = 20)
    private String outpatientNo;

    /**
     * 患者姓名
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 性别: MALE(男), FEMALE(女)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    /**
     * 年龄
     */
    @Column(name = "age", nullable = false)
    private Integer age;

    /**
     * 联系电话
     */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // ============================================
    // 报告信息字段
    // ============================================

    /**
     * 报告医生
     */
    @Column(name = "report_doctor", nullable = false, length = 50)
    private String reportDoctor;

    /**
     * 填报日期
     */
    @Column(name = "fill_date", nullable = false)
    private LocalDate fillDate;

    // ============================================
    // 审核信息字段
    // ============================================

    /**
     * 审核日期
     */
    @Column(name = "audit_date")
    private LocalDate auditDate;

    /**
     * 审核人姓名
     */
    @Column(name = "auditor", length = 50)
    private String auditor;

    /**
     * 审核人ID (关联sys_user)
     */
    @Column(name = "auditor_id", length = 36)
    private String auditorId;

    /**
     * 状态: PENDING(待审核), APPROVED(已审核), REJECTED(审核不通过)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    /**
     * 审核备注
     */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    // ============================================
    // 时间戳字段
    // ============================================

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

    // ============================================
    // 逻辑删除标记 (必须字段)
    // ============================================

    /**
     * 逻辑删除标记 (false-未删除, true-已删除)
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // ============================================
    // 关联关系
    // ============================================

    /**
     * 审核人关联 (只读关联，用于查询时获取审核人完整信息)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SysUser auditorUser;

    // ============================================
    // 生命周期回调
    // ============================================

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
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    // ============================================
    // 枚举定义
    // ============================================

    /**
     * 报告状态枚举
     */
    public enum ReportStatus {
        PENDING("待审核"),      // 待审核
        APPROVED("已审核"),     // 已审核
        REJECTED("审核不通过");  // 审核不通过

        private final String description;

        ReportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        MALE("男"),   // 男
        FEMALE("女"); // 女

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
