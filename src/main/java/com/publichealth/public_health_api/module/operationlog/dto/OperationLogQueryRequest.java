package com.publichealth.public_health_api.module.operationlog.dto;

import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogQueryRequest {

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 操作状态
     */
    private OperationStatus status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 关键字（操作描述或方法名模糊匹配）
     */
    private String keyword;

    /**
     * IP地址
     */
    private String ipAddress;
}
