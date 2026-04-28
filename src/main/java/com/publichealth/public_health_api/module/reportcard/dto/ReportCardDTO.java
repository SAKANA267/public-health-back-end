package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardDiagnosis;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 传染病报告卡数据传输对象
 * 用于返回给前端的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardDTO {

    private String id;
    private String cardNumber;
    private ReportCard.ReportCategory reportCategory;
    private ReportCard.ReportStatus reportStatus;
    private String hospitalArea;
    private String department;
    private String inpatientNo;
    private String outpatientNo;
    private String doctorName;
    private LocalDate fillDate;
    private String patientName;
    private String diseaseName;
    private ReportCard.AuditStatus auditStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private PatientInfoDTO patientInfo;
    private DiagnosisInfoDTO diagnosisInfo;
    private AuditInfoDTO auditInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfoDTO {
        private String id;
        private String patientName;
        private String idCard;
        private LocalDate birthday;
        private ReportCardPatient.Gender gender;
        private Integer age;
        private String phone;
        private String parentName;
        private String workUnit;
        private ReportCardPatient.AddressType addressType;
        private String detailAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisInfoDTO {
        private String id;
        private String diseaseName;
        private String diagnosisCode;
        private ReportCardDiagnosis.PatientBelong patientBelong;
        private List<String> crowdCategories;
        private ReportCardDiagnosis.CaseType caseType;
        private ReportCardDiagnosis.CaseAttribute caseAttribute;
        private LocalDate onsetDate;
        private LocalDate diagnosisDate;
        private LocalDate deathDate;
        private String remark;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditInfoDTO {
        private String id;
        private String auditorId;
        private String auditorName;
        private ReportCardAudit.AuditStatus auditStatus;
        private LocalDateTime auditDate;
        private String rejectReason;
        private ReportCardAudit.AssignStatus assignStatus;
        private String assigneeId;
    }

    public static ReportCardDTO fromEntity(
            ReportCard reportCard,
            ReportCardPatient patient,
            ReportCardDiagnosis diagnosis,
            ReportCardAudit audit
    ) {
        if (reportCard == null) {
            return null;
        }
        ReportCardDTO dto = new ReportCardDTO();
        dto.setId(reportCard.getId());
        dto.setCardNumber(reportCard.getCardNumber());
        dto.setReportCategory(reportCard.getReportCategory());
        dto.setReportStatus(reportCard.getReportStatus());
        dto.setHospitalArea(reportCard.getHospitalArea());
        dto.setDepartment(reportCard.getDepartment());
        dto.setInpatientNo(reportCard.getInpatientNo());
        dto.setOutpatientNo(reportCard.getOutpatientNo());
        dto.setDoctorName(reportCard.getDoctorName());
        dto.setFillDate(reportCard.getFillDate());
        dto.setPatientName(reportCard.getPatientName());
        dto.setDiseaseName(reportCard.getDiseaseName());
        dto.setAuditStatus(reportCard.getAuditStatus());
        dto.setCreateTime(reportCard.getCreateTime());
        dto.setUpdateTime(reportCard.getUpdateTime());

        if (patient != null) {
            PatientInfoDTO patientInfo = new PatientInfoDTO();
            patientInfo.setId(patient.getId());
            patientInfo.setPatientName(patient.getPatientName());
            patientInfo.setIdCard(patient.getIdCard());
            patientInfo.setBirthday(patient.getBirthday());
            patientInfo.setGender(patient.getGender());
            patientInfo.setAge(patient.getAge());
            patientInfo.setPhone(patient.getPhone());
            patientInfo.setParentName(patient.getParentName());
            patientInfo.setWorkUnit(patient.getWorkUnit());
            patientInfo.setAddressType(patient.getAddressType());
            patientInfo.setDetailAddress(patient.getDetailAddress());
            dto.setPatientInfo(patientInfo);
        }

        if (diagnosis != null) {
            DiagnosisInfoDTO diagnosisInfo = new DiagnosisInfoDTO();
            diagnosisInfo.setId(diagnosis.getId());
            diagnosisInfo.setDiseaseName(diagnosis.getDiseaseName());
            diagnosisInfo.setDiagnosisCode(diagnosis.getDiagnosisCode());
            diagnosisInfo.setPatientBelong(diagnosis.getPatientBelong());
            diagnosisInfo.setCrowdCategories(diagnosis.getCrowdCategories());
            diagnosisInfo.setCaseType(diagnosis.getCaseType());
            diagnosisInfo.setCaseAttribute(diagnosis.getCaseAttribute());
            diagnosisInfo.setOnsetDate(diagnosis.getOnsetDate());
            diagnosisInfo.setDiagnosisDate(diagnosis.getDiagnosisDate());
            diagnosisInfo.setDeathDate(diagnosis.getDeathDate());
            diagnosisInfo.setRemark(diagnosis.getRemark());
            dto.setDiagnosisInfo(diagnosisInfo);
        }

        if (audit != null) {
            AuditInfoDTO auditInfo = new AuditInfoDTO();
            auditInfo.setId(audit.getId());
            auditInfo.setAuditorId(audit.getAuditorId());
            auditInfo.setAuditorName(audit.getAuditorName());
            auditInfo.setAuditStatus(audit.getAuditStatus());
            auditInfo.setAuditDate(audit.getAuditDate());
            auditInfo.setRejectReason(audit.getRejectReason());
            auditInfo.setAssignStatus(audit.getAssignStatus());
            auditInfo.setAssigneeId(audit.getAssigneeId());
            dto.setAuditInfo(auditInfo);
        }

        return dto;
    }

    public static ReportCardDTO forList(ReportCard reportCard) {
        if (reportCard == null) {
            return null;
        }
        ReportCardDTO dto = new ReportCardDTO();
        dto.setId(reportCard.getId());
        dto.setCardNumber(reportCard.getCardNumber());
        dto.setReportCategory(reportCard.getReportCategory());
        dto.setReportStatus(reportCard.getReportStatus());
        dto.setHospitalArea(reportCard.getHospitalArea());
        dto.setDepartment(reportCard.getDepartment());
        dto.setInpatientNo(reportCard.getInpatientNo());
        dto.setOutpatientNo(reportCard.getOutpatientNo());
        dto.setDoctorName(reportCard.getDoctorName());
        dto.setFillDate(reportCard.getFillDate());
        dto.setPatientName(reportCard.getPatientName());
        dto.setDiseaseName(reportCard.getDiseaseName());
        dto.setAuditStatus(reportCard.getAuditStatus());
        dto.setCreateTime(reportCard.getCreateTime());
        dto.setUpdateTime(reportCard.getUpdateTime());
        return dto;
    }
}
