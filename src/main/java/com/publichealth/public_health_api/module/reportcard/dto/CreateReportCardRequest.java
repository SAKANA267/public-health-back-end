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
 * 创建传染病报告卡请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportCardRequest {

    @NotBlank(message = "院区不能为空")
    @Size(max = 50, message = "院区不能超过50个字符")
    private String hospitalArea;

    @NotBlank(message = "科室不能为空")
    @Size(max = 50, message = "科室不能超过50个字符")
    private String department;

    @Size(max = 20, message = "住院号不能超过20个字符")
    private String inpatientNo;

    @Size(max = 20, message = "门诊号不能超过20个字符")
    private String outpatientNo;

    @NotBlank(message = "填卡医生不能为空")
    @Size(max = 50, message = "填卡医生不能超过50个字符")
    private String doctorName;

    @NotNull(message = "填卡日期不能为空")
    @PastOrPresent(message = "填卡日期不能是未来日期")
    private LocalDate fillDate;

    @Size(max = 50, message = "卡片编号不能超过50个字符")
    private String cardNumber;

    private ReportCard.ReportCategory reportCategory;

    @Valid
    @NotNull(message = "患者信息不能为空")
    private PatientInfo patientInfo;

    @Valid
    @NotNull(message = "诊断信息不能为空")
    private DiagnosisInfo diagnosisInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        @NotBlank(message = "患者姓名不能为空")
        @Size(max = 50, message = "患者姓名不能超过50个字符")
        private String patientName;

        @NotBlank(message = "身份证号不能为空")
        @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
                message = "身份证号格式不正确")
        private String idCard;

        private LocalDate birthday;

        private ReportCardPatient.Gender gender;

        @Min(value = 0, message = "年龄不能小于0")
        @Max(value = 150, message = "年龄不能大于150")
        private Integer age;

        @NotBlank(message = "联系电话不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
        @Size(max = 20, message = "联系电话不能超过20个字符")
        private String phone;

        @Size(max = 50, message = "家长姓名不能超过50个字符")
        private String parentName;

        @Size(max = 100, message = "工作单位不能超过100个字符")
        private String workUnit;

        private ReportCardPatient.AddressType addressType;

        @NotBlank(message = "详细地址不能为空")
        @Size(max = 200, message = "详细地址不能超过200个字符")
        private String detailAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisInfo {
        @NotBlank(message = "疾病名称不能为空")
        @Size(max = 100, message = "疾病名称不能超过100个字符")
        private String diseaseName;

        @Size(max = 50, message = "疾病代码不能超过50个字符")
        private String diagnosisCode;

        private ReportCardDiagnosis.PatientBelong patientBelong;

        private List<String> crowdCategories;

        private ReportCardDiagnosis.CaseType caseType;

        private ReportCardDiagnosis.CaseAttribute caseAttribute;

        private LocalDate onsetDate;

        @NotNull(message = "诊断日期不能为空")
        private LocalDate diagnosisDate;

        private LocalDate deathDate;

        @Size(max = 1000, message = "备注不能超过1000个字符")
        private String remark;
    }
}
