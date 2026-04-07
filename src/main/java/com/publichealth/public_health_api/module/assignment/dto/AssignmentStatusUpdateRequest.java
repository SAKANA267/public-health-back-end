package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务状态更新请求DTO
 */
@Data
public class AssignmentStatusUpdateRequest {

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String assignmentId;

    /**
     * 新状态
     */
    @NotNull(message = "状态不能为空")
    private AssignmentStatus status;

    /**
     * 备注/原因
     */
    private String remark;

    /**
     * 乐观锁版本号
     */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
