package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报卡分配请求DTO
 */
@Data
public class AssignmentRequest {

    /**
     * 报卡ID
     */
    @NotBlank(message = "报卡ID不能为空")
    private String reportCardId;

    /**
     * 审核组ID
     */
    @NotBlank(message = "审核组ID不能为空")
    private String auditGroupId;

    /**
     * 优先级
     */
    private AssignmentPriority priority = AssignmentPriority.NORMAL;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 分配备注
     */
    private String remark;

    /**
     * 规则ID (如果是自动分配)
     */
    private String ruleId;
}
