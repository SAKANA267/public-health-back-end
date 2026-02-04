package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 传染病报告卡数据传输对象
 * 用于返回给前端的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardDTO {

    // ============================================
    // 基础信息字段
    // ============================================

    private String id;
    private String hospitalArea;
    private String department;
    private String diagnosisName;

    // ============================================
    // 患者信息字段
    // ============================================

    private String inpatientNo;
    private String outpatientNo;
    private String name;
    private ReportCard.Gender gender;
    private Integer age;
    private String phone;

    // ============================================
    // 报告信息字段
    // ============================================

    private String reportDoctor;
    private LocalDate fillDate;

    // ============================================
    // 审核信息字段
    // ============================================

    private LocalDate auditDate;
    private String auditor;
    private String auditorId;
    private ReportCard.ReportStatus status;
    private String remark;

    // ============================================
    // 时间戳字段
    // ============================================

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 从实体转换为DTO
     */
    public static ReportCardDTO fromEntity(ReportCard entity) {
        if (entity == null) {
            return null;
        }
        ReportCardDTO dto = new ReportCardDTO();
        dto.setId(entity.getId());
        dto.setHospitalArea(entity.getHospitalArea());
        dto.setDepartment(entity.getDepartment());
        dto.setDiagnosisName(entity.getDiagnosisName());
        dto.setInpatientNo(entity.getInpatientNo());
        dto.setOutpatientNo(entity.getOutpatientNo());
        dto.setName(entity.getName());
        dto.setGender(entity.getGender());
        dto.setAge(entity.getAge());
        dto.setPhone(entity.getPhone());
        dto.setReportDoctor(entity.getReportDoctor());
        dto.setFillDate(entity.getFillDate());
        dto.setAuditDate(entity.getAuditDate());
        dto.setAuditor(entity.getAuditor());
        dto.setAuditorId(entity.getAuditorId());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
