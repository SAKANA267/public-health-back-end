package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 创建传染病报告卡请求DTO
 * 用于接收创建时的请求数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportCardRequest {

    // ============================================
    // 基础信息字段
    // ============================================

    /**
     * 院区
     */
    @NotBlank(message = "院区不能为空")
    @Size(max = 50, message = "院区不能超过50个字符")
    private String hospitalArea;

    /**
     * 科室
     */
    @NotBlank(message = "科室不能为空")
    @Size(max = 50, message = "科室不能超过50个字符")
    private String department;

    /**
     * 诊断名称
     */
    @NotBlank(message = "诊断名称不能为空")
    @Size(max = 100, message = "诊断名称不能超过100个字符")
    private String diagnosisName;

    // ============================================
    // 患者信息字段
    // ============================================

    /**
     * 住院号
     */
    @NotBlank(message = "住院号不能为空")
    @Size(max = 20, message = "住院号不能超过20个字符")
    private String inpatientNo;

    /**
     * 门诊号
     */
    @NotBlank(message = "门诊号不能为空")
    @Size(max = 20, message = "门诊号不能超过20个字符")
    private String outpatientNo;

    /**
     * 患者姓名
     */
    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 50, message = "患者姓名不能超过50个字符")
    private String name;

    /**
     * 性别
     */
    @NotNull(message = "性别不能为空")
    private ReportCard.Gender gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    // ============================================
    // 报告信息字段
    // ============================================

    /**
     * 报告医生
     */
    @NotBlank(message = "报告医生不能为空")
    @Size(max = 50, message = "报告医生不能超过50个字符")
    private String reportDoctor;

    /**
     * 填报日期
     */
    @NotNull(message = "填报日期不能为空")
    @PastOrPresent(message = "填报日期不能是未来日期")
    private LocalDate fillDate;
}
