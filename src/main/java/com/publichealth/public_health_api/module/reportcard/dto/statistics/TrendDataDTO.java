package com.publichealth.public_health_api.module.reportcard.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 趋势数据DTO
 * 用于报卡时间趋势统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataDTO {

    /**
     * 时间标签（如"周一"、"1月"、"第1周"）
     */
    private String label;

    /**
     * 该时段的报卡数量
     */
    private Long value;
}
