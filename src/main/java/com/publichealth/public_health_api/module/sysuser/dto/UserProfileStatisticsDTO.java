package com.publichealth.public_health_api.module.sysuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人中心统计DTO
 * 用于Profile页面的统计卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileStatisticsDTO {

    /**
     * 完成任务数（当前用户完成的审核任务数）
     */
    private Long completedTasks;

    /**
     * 审核记录数（当前用户的审核操作记录数）
     */
    private Long auditRecords;

    /**
     * 工作时长（小时）
     */
    private Double workHours;

    /**
     * 审核准确率（百分比，如98.5）
     */
    private Double accuracyRate;
}
