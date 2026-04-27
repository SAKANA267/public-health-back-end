package com.publichealth.public_health_api.module.sysuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 贡献日历数据DTO
 * 用于Profile页面的活跃记录/贡献日历
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDTO {

    /**
     * 日期 (YYYY-MM-DD格式)
     */
    private String date;

    /**
     * 当日操作数
     */
    private Long count;
}
