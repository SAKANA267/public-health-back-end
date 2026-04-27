package com.publichealth.public_health_api.module.sysuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户最近活动DTO
 * 用于Profile页面的最近活动列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecentActivityDTO {

    /**
     * 操作描述
     */
    private String action;

    /**
     * 操作对象
     */
    private String target;

    /**
     * 操作时间（ISO格式）
     */
    private String time;
}
