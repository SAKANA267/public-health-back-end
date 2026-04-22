package com.publichealth.public_health_api.module.cdcupload.dto;

import com.publichealth.public_health_api.module.cdcupload.entity.CdcUpload;
import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CDC上报数据传输对象
 * 组合 CdcUpload + ReportCard 字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUploadDTO {

    // ============================================
    // 上报记录信息
    // ============================================

    private String id;
    private String reportCardId;

    // ============================================
    // 报告卡基础信息
    // ============================================

    private String hospitalArea;
    private String department;
    private String diagnosisName;
    private String inpatientNo;
    private String outpatientNo;

    // ============================================
    // 患者信息
    // ============================================

    private String name;
    private ReportCard.Gender gender;
    private Integer age;
    private String phone;

    // ============================================
    // 报告信息
    // ============================================

    private String reportDoctor;
    private LocalDate fillDate;

    // ============================================
    // 审核信息
    // ============================================

    private String auditor;
    private LocalDate auditDate;

    // ============================================
    // 上报状态信息
    // ============================================

    private UploadStatus uploadStatus;
    private LocalDateTime uploadTime;
    private String uploadOperator;
    private String uploadOperatorName;
    private String failReason;
    private Integer retryCount;
    private String cdcSerialNo;

    /**
     * 从 CdcUpload 实体和 ReportCard 实体转换为 DTO
     */
    public static CdcUploadDTO fromEntity(CdcUpload upload, ReportCard reportCard) {
        CdcUploadDTO dto = new CdcUploadDTO();
        dto.setId(upload.getId());
        dto.setReportCardId(upload.getReportCardId());
        dto.setUploadStatus(upload.getUploadStatus());
        dto.setUploadTime(upload.getUploadTime());
        dto.setUploadOperator(upload.getUploadOperator());
        dto.setUploadOperatorName(upload.getUploadOperatorName());
        dto.setFailReason(upload.getFailReason());
        dto.setRetryCount(upload.getRetryCount());
        dto.setCdcSerialNo(upload.getCdcSerialNo());

        if (reportCard != null) {
            dto.setHospitalArea(reportCard.getHospitalArea());
            dto.setDepartment(reportCard.getDepartment());
            dto.setDiagnosisName(reportCard.getDiagnosisName());
            dto.setInpatientNo(reportCard.getInpatientNo());
            dto.setOutpatientNo(reportCard.getOutpatientNo());
            dto.setName(reportCard.getName());
            dto.setGender(reportCard.getGender());
            dto.setAge(reportCard.getAge());
            dto.setPhone(reportCard.getPhone());
            dto.setReportDoctor(reportCard.getReportDoctor());
            dto.setFillDate(reportCard.getFillDate());
            dto.setAuditor(reportCard.getAuditor());
            dto.setAuditDate(reportCard.getAuditDate());
        }
        return dto;
    }
}
