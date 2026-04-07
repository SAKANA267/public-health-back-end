package com.publichealth.public_health_api.module.assignment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作统计响应DTO
 */
@Data
public class WorkStatsResponse {

    private String id;
    private String auditGroupId;
    private String auditGroupName;
    private String auditGroupCode;
    private Integer totalAssigned;
    private Integer totalCompleted;
    private Integer totalCancelled;
    private Integer pendingCount;
    private Integer inProgressCount;
    private Integer currentTaskCount;
    private Integer avgProcessTime;
    private LocalDateTime lastTaskTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
