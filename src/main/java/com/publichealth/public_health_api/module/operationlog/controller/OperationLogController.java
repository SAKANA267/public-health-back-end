package com.publichealth.public_health_api.module.operationlog.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogDTO;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogQueryRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsResponse;
import com.publichealth.public_health_api.module.operationlog.service.OperationLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志控制器
 * 提供操作日志查询、统计等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志列表
     * GET /api/operation-logs?page=1&size=10&userId=xxx&module=xxx&status=SUCCESS
     */
    @GetMapping
    public ApiResponse<PageResult<OperationLogDTO>> queryLogs(OperationLogQueryRequest request) {
        log.info("查询操作日志: {}", request);
        PageResult<OperationLogDTO> result = operationLogService.queryLogs(request);
        return ApiResponse.success(result);
    }

    /**
     * 查询操作日志详情
     * GET /api/operation-logs/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<OperationLogDTO> getLogById(@PathVariable String id) {
        log.info("查询操作日志详情: id={}", id);
        OperationLogDTO log = operationLogService.getLogById(id);
        return ApiResponse.success(log);
    }

    /**
     * 查询指定用户的操作日志
     * GET /api/operation-logs/user/{userId}?page=1&size=10
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResult<OperationLogDTO>> getLogsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("查询用户操作日志: userId={}, page={}, size={}", userId, page, size);
        PageResult<OperationLogDTO> result = operationLogService.getLogsByUserId(userId, page, size);
        return ApiResponse.success(result);
    }

    /**
     * 查询指定用户的失败操作
     * GET /api/operation-logs/user/{userId}/failed
     */
    @GetMapping("/user/{userId}/failed")
    public ApiResponse<List<OperationLogDTO>> getFailedOperationsByUserId(@PathVariable String userId) {
        log.info("查询用户失败操作: userId={}", userId);
        List<OperationLogDTO> logs = operationLogService.getFailedOperationsByUserId(userId);
        return ApiResponse.success(logs);
    }

    /**
     * 统计分析操作日志
     * POST /api/operation-logs/statistics
     */
    @PostMapping("/statistics")
    public ApiResponse<OperationLogStatsResponse> statistics(
            @Valid @RequestBody OperationLogStatsRequest request) {
        log.info("统计分析操作日志: {}", request);
        OperationLogStatsResponse stats = operationLogService.statistics(request);
        return ApiResponse.success(stats);
    }

    /**
     * 清理历史日志
     * DELETE /api/operation-logs/clean?beforeTime=2024-01-01T00:00:00
     */
    @DeleteMapping("/clean")
    public ApiResponse<Integer> cleanLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        log.info("清理历史日志: beforeTime={}", beforeTime);
        int deletedCount = operationLogService.cleanLogsBefore(beforeTime);
        return ApiResponse.success("已清理 " + deletedCount + " 条日志", deletedCount);
    }
}
