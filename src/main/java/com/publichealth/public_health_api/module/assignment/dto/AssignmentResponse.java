package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentPriority;
import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报卡分配响应DTO
 */
@Data
public class AssignmentResponse {

    private String id;
    private String reportCardId;
    private String auditGroupId;
    private String auditGroupName;
    private String assignerId;
    private String assignerName;
    private AssignmentStatus status;
    private String statusDescription;
    private LocalDateTime assignTime;
    private LocalDateTime deadline;
    private LocalDateTime acceptTime;
    private LocalDateTime completeTime;
    private AssignmentPriority priority;
    private String priorityDescription;
    private String remark;
    private String rejectReason;
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息
    private String reportCardInpatientNo;
    private String reportCardPatientName;
    private String reportCardDiagnosisName;
}
