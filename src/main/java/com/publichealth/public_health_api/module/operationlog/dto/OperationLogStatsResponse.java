package com.publichealth.public_health_api.module.operationlog.dto;

import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 操作日志统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLogStatsResponse {

    /**
     * 总操作数
     */
    private Long totalOperations;

    /**
     * 成功操作数
     */
    private Long successCount;

    /**
     * 失败操作数
     */
    private Long failureCount;

    /**
     * 成功率
     */
    private Double successRate;

    /**
     * 平均耗时（毫秒）
     */
    private Double avgCostTime;

    /**
     * 最大耗时（毫秒）
     */
    private Long maxCostTime;

    /**
     * 各模块统计（模块名 -> 操作数）
     */
    private Map<String, Long> moduleStats;

    /**
     * 各操作类型统计（操作类型 -> 操作数）
     */
    private Map<OperationType, Long> operationTypeStats;

    /**
     * 时间段统计（日期 -> 操作数）
     */
    private Map<String, Long> timeDistribution;
}
