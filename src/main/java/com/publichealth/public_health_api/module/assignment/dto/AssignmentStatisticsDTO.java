package com.publichealth.public_health_api.module.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务分配统计DTO
 * 用于TaskManagement页面的统计卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStatisticsDTO {

    /**
     * 待处理任务数 (status = 'PENDING')
     */
    private Long pending;

    /**
     * 处理中任务数 (status = 'IN_PROGRESS')
     */
    private Long inProgress;

    /**
     * 已完成任务数 (status = 'COMPLETED')
     */
    private Long completed;

    /**
     * 已取消任务数 (status = 'CANCELLED')
     */
    private Long cancelled;

    /**
     * 全部任务数
     */
    private Long total;
}
