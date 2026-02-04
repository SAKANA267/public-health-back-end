package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新传染病报告卡请求DTO
 * 用于接收更新时的请求数据
 * 注意: 仅允许更新待审核状态的报告卡
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportCardRequest {

    // ============================================
    // 可更新的基础信息字段
    // ============================================

    /**
     * 诊断名称 (可选)
     */
    @Size(max = 100, message = "诊断名称不能超过100个字符")
    private String diagnosisName;

    /**
     * 联系电话 (可选)
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    /**
     * 报告医生 (可选)
     */
    @Size(max = 50, message = "报告医生不能超过50个字符")
    private String reportDoctor;
}
