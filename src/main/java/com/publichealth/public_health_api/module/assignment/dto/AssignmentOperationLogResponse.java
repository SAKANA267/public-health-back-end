package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.AssignmentOperationLog.OperationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志响应DTO
 */
@Data
public class AssignmentOperationLogResponse {

    private String id;
    private String assignmentId;
    private String reportCardId;
    private OperationType operationType;
    private String operationTypeDescription;
    private String operatorId;
    private String operatorName;
    private String beforeStatus;
    private String afterStatus;
    private String operationDetail;
    private String remark;
    private LocalDateTime createTime;
}
