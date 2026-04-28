package com.publichealth.public_health_api.module.cdcupload.dto;

import com.publichealth.public_health_api.module.cdcupload.entity.CdcUpload;
import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CDC上报数据传输对象
 * 组合 CdcUpload + ReportCard + ReportCardPatient + ReportCardAudit 字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUploadDTO {

    private String id;
    private String reportCardId;

    private String hospitalArea;
    private String department;
    private String diseaseName;
    private String inpatientNo;
    private String outpatientNo;

    private String patientName;
    private ReportCardPatient.Gender gender;
    private Integer age;
    private String phone;

    private String doctorName;
    private LocalDate fillDate;

    private String auditorName;
    private LocalDateTime auditDate;

    private UploadStatus uploadStatus;
    private LocalDateTime uploadTime;
    private String uploadOperator;
    private String uploadOperatorName;
    private String failReason;
    private Integer retryCount;
    private String cdcSerialNo;

    public static CdcUploadDTO fromEntity(CdcUpload upload, ReportCard reportCard,
                                           ReportCardPatient patient, ReportCardAudit audit) {
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
            dto.setDiseaseName(reportCard.getDiseaseName());
            dto.setInpatientNo(reportCard.getInpatientNo());
            dto.setOutpatientNo(reportCard.getOutpatientNo());
            dto.setPatientName(reportCard.getPatientName());
            dto.setDoctorName(reportCard.getDoctorName());
            dto.setFillDate(reportCard.getFillDate());
        }

        if (patient != null) {
            dto.setGender(patient.getGender());
            dto.setAge(patient.getAge());
            dto.setPhone(patient.getPhone());
        }

        if (audit != null) {
            dto.setAuditorName(audit.getAuditorName());
            dto.setAuditDate(audit.getAuditDate());
        }

        return dto;
    }
}
