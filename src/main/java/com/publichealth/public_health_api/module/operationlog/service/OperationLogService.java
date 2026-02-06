package com.publichealth.public_health_api.module.operationlog.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogDTO;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogQueryRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsResponse;
import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 异步保存操作日志
     *
     * @param operationLog 操作日志实体
     */
    void saveAsync(OperationLog operationLog);

    /**
     * 分页查询操作日志
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<OperationLogDTO> queryLogs(OperationLogQueryRequest request);

    /**
     * 根据ID查询操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志DTO
     */
    OperationLogDTO getLogById(String id);

    /**
     * 查询指定用户的操作日志
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页结果
     */
    PageResult<OperationLogDTO> getLogsByUserId(String userId, int page, int size);

    /**
     * 查询指定用户的失败操作
     *
     * @param userId 用户ID
     * @return 失败操作列表
     */
    List<OperationLogDTO> getFailedOperationsByUserId(String userId);

    /**
     * 统计操作日志
     *
     * @param request 统计请求
     * @return 统计结果
     */
    OperationLogStatsResponse statistics(OperationLogStatsRequest request);

    /**
     * 清理指定时间之前的日志
     *
     * @param beforeTime 时间点
     * @return 删除的日志数量
     */
    int cleanLogsBefore(java.time.LocalDateTime beforeTime);
}
