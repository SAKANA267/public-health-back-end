package com.publichealth.public_health_api.module.reportcard.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报卡统计DTO
 * 用于Dashboard页面的报卡统计卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardStatisticsDTO {

    /**
     * 报卡总数（所有audit_status）
     */
    private Long total;

    /**
     * 待审核数量 (audit_status = 'PENDING')
     */
    private Long pending;

    /**
     * 已审核通过数量 (audit_status = 'APPROVED')
     */
    private Long approved;

    /**
     * 审核不通过数量 (audit_status = 'REJECTED')
     */
    private Long rejected;

    /**
     * 今日新增数量 (当日create_time的记录数)
     */
    private Long todayNew;
}
