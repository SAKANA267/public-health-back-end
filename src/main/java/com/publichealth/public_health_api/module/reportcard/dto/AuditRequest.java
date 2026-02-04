package com.publichealth.public_health_api.module.reportcard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核请求DTO
 * 用于审核通过/拒绝操作
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {

    /**
     * 审核人ID
     */
    @NotBlank(message = "审核人ID不能为空")
    private String auditorId;

    /**
     * 审核备注
     * 拒绝时建议填写原因
     */
    private String remark;
}
