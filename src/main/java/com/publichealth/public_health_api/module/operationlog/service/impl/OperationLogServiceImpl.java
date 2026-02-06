package com.publichealth.public_health_api.module.operationlog.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogDTO;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogQueryRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsRequest;
import com.publichealth.public_health_api.module.operationlog.dto.OperationLogStatsResponse;
import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;
import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import com.publichealth.public_health_api.module.operationlog.repository.OperationLogRepository;
import com.publichealth.public_health_api.module.operationlog.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * 异步保存操作日志
     * 使用名为 "operationLogExecutor" 的线程池执行
     */
    @Async("operationLogExecutor")
    @Override
    @Transactional
    public void saveAsync(OperationLog operationLog) {
        try {
            operationLogRepository.save(operationLog);
            log.debug("操作日志保存成功: id={}, module={}, operation={}",
                    operationLog.getId(), operationLog.getModule(), operationLog.getOperation());
        } catch (Exception e) {
            // 日志保存失败不应影响主业务，仅记录错误
            log.error("操作日志保存失败: module={}, operation={}, error={}",
                    operationLog.getModule(), operationLog.getOperation(), e.getMessage(), e);
        }
    }

    @Override
    public PageResult<OperationLogDTO> queryLogs(OperationLogQueryRequest request) {
        log.info("查询操作日志: page={}, size={}, userId={}, module={}, operationType={}, status={}",
                request.getPage(), request.getSize(), request.getUserId(), request.getModule(),
                request.getOperationType(), request.getStatus());

        // 构建分页参数（按创建时间倒序）
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        // 多条件查询
        Page<OperationLog> page = operationLogRepository.findByConditions(
                request.getUserId(),
                request.getModule(),
                request.getOperationType(),
                request.getStatus(),
                request.getStartTime(),
                request.getEndTime(),
                request.getKeyword(),
                request.getIpAddress(),
                pageable
        );

        // 转换为DTO
        List<OperationLogDTO> dtoList = page.getContent().stream()
                .map(OperationLogDTO::fromEntity)
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                dtoList
        );
    }

    @Override
    public OperationLogDTO getLogById(String id) {
        OperationLog operationLog = operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("操作日志不存在"));
        return OperationLogDTO.fromEntity(operationLog);
    }

    @Override
    public PageResult<OperationLogDTO> getLogsByUserId(String userId, int page, int size) {
        log.info("查询用户操作日志: userId={}, page={}, size={}", userId, page, size);

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        Page<OperationLog> logPage = operationLogRepository.findByUserId(userId, pageable);

        List<OperationLogDTO> dtoList = logPage.getContent().stream()
                .map(OperationLogDTO::fromEntity)
                .collect(Collectors.toList());

        return PageResult.of(page, size, logPage.getTotalElements(), dtoList);
    }

    @Override
    public List<OperationLogDTO> getFailedOperationsByUserId(String userId) {
        log.info("查询用户失败操作: userId={}", userId);

        List<OperationLog> logs = operationLogRepository.findByUserIdAndStatus(
                userId,
                OperationStatus.FAILURE
        );

        return logs.stream()
                .map(OperationLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OperationLogStatsResponse statistics(OperationLogStatsRequest request) {
        log.info("统计操作日志: startTime={}, endTime={}, userId={}, module={}",
                request.getStartTime(), request.getEndTime(), request.getUserId(), request.getModule());

        // 计算总操作数
        long totalOperations = operationLogRepository.countByTimeRange(
                request.getStartTime(),
                request.getEndTime()
        );

        // 计算失败操作数
        long failureCount = operationLogRepository.countByStatus(OperationStatus.FAILURE);

        // 计算成功操作数
        long successCount = totalOperations - failureCount;

        // 计算成功率
        double successRate = totalOperations > 0 ? (double) successCount / totalOperations * 100 : 0;

        // 获取平均耗时
        Double avgCostTime = operationLogRepository.getAvgCostTime();

        // 获取最大耗时
        Long maxCostTime = operationLogRepository.getMaxCostTime();

        // 获取各模块统计
        List<Object[]> moduleStats = operationLogRepository.countByModuleGroupBy();
        Map<String, Long> moduleStatsMap = moduleStats.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // 获取各操作类型统计
        List<Object[]> operationTypeStats = operationLogRepository.countByOperationTypeGroupBy();
        Map<OperationType, Long> operationTypeStatsMap = operationTypeStats.stream()
                .collect(Collectors.toMap(
                        row -> (OperationType) row[0],
                        row -> (Long) row[1]
                ));

        return OperationLogStatsResponse.builder()
                .totalOperations(totalOperations)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(successRate)
                .avgCostTime(avgCostTime)
                .maxCostTime(maxCostTime)
                .moduleStats(moduleStatsMap)
                .operationTypeStats(operationTypeStatsMap)
                .build();
    }

    @Override
    @Transactional
    public int cleanLogsBefore(LocalDateTime beforeTime) {
        log.info("清理历史日志: beforeTime={}", beforeTime);

        int deletedCount = operationLogRepository.deleteByCreateTimeBefore(beforeTime);

        log.info("历史日志清理完成: deletedCount={}", deletedCount);
        return deletedCount;
    }
}
