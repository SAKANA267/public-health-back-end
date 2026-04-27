package com.publichealth.public_health_api.module.reportcard.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 最近活动DTO
 * 用于Dashboard页面的最近审核活动列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {

    /**
     * 操作人姓名
     */
    private String user;

    /**
     * 操作类型（"提交报卡"/"审核通过"/"审核驳回"等）
     */
    private String action;

    /**
     * 操作对象（如"新冠肺炎-张某某"）
     */
    private String target;

    /**
     * 操作时间（ISO格式）
     */
    private String time;

    /**
     * 状态类型（用于前端显示不同颜色）
     */
    private String status;
}
