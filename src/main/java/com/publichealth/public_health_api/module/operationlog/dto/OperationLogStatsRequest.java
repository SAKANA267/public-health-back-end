package com.publichealth.public_health_api.module.operationlog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志统计请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogStatsRequest {

    /**
     * 统计开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 统计结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 用户ID（可选，用于统计特定用户）
     */
    private String userId;

    /**
     * 模块名称（可选，用于统计特定模块）
     */
    private String module;
}
