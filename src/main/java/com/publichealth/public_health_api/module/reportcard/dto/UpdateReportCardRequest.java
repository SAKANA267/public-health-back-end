package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardDiagnosis;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 更新传染病报告卡请求DTO
 * 仅允许更新待审核状态的报告卡
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportCardRequest {

    @Size(max = 50, message = "卡片编号不能超过50个字符")
    private String cardNumber;

    private ReportCard.ReportCategory reportCategory;

    private ReportCard.ReportStatus reportStatus;

    @Size(max = 50, message = "填卡医生不能超过50个字符")
    private String doctorName;

    @Valid
    private PatientInfoUpdate patientInfo;

    @Valid
    private DiagnosisInfoUpdate diagnosisInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfoUpdate {
        @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
        @Size(max = 20, message = "联系电话不能超过20个字符")
        private String phone;

        private LocalDate birthday;

        private ReportCardPatient.Gender gender;

        @Min(value = 0, message = "年龄不能小于0")
        @Max(value = 150, message = "年龄不能大于150")
        private Integer age;

        @Size(max = 50, message = "家长姓名不能超过50个字符")
        private String parentName;

        @Size(max = 100, message = "工作单位不能超过100个字符")
        private String workUnit;

        private ReportCardPatient.AddressType addressType;

        @Size(max = 200, message = "详细地址不能超过200个字符")
        private String detailAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisInfoUpdate {
        @Size(max = 100, message = "疾病名称不能超过100个字符")
        private String diseaseName;

        @Size(max = 50, message = "疾病代码不能超过50个字符")
        private String diagnosisCode;

        private ReportCardDiagnosis.PatientBelong patientBelong;

        private List<String> crowdCategories;

        private ReportCardDiagnosis.CaseType caseType;

        private ReportCardDiagnosis.CaseAttribute caseAttribute;

        private LocalDate onsetDate;

        private LocalDate diagnosisDate;

        private LocalDate deathDate;

        @Size(max = 1000, message = "备注不能超过1000个字符")
        private String remark;
    }
}
