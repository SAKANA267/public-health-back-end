package com.publichealth.public_health_api.module.assignment.service.impl;

import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.assignment.dto.WorkStatsResponse;
import com.publichealth.public_health_api.module.assignment.entity.AuditGroupWorkStats;
import com.publichealth.public_health_api.module.assignment.repository.AuditGroupWorkStatsRepository;
import com.publichealth.public_health_api.module.assignment.service.WorkStatsService;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作统计Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkStatsServiceImpl implements WorkStatsService {

    private final AuditGroupWorkStatsRepository statsRepository;
    private final AuditGroupRepository auditGroupRepository;

    @Override
    public WorkStatsResponse getGroupStats(String auditGroupId) {
        AuditGroupWorkStats stats = statsRepository.findByAuditGroupIdAndDeletedFalse(auditGroupId)
                .orElseGet(() -> createStats(auditGroupId));

        return convertToResponse(stats);
    }

    @Override
    public List<WorkStatsResponse> getAllGroupStats() {
        List<AuditGroupWorkStats> statsList = statsRepository.findAllActiveOrderByCurrentTaskCount();

        return statsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String getLeastLoadedGroupId() {
        return statsRepository.findLeastLoadedGroup()
                .map(AuditGroupWorkStats::getAuditGroupId)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateStats(String auditGroupId) {
        AuditGroupWorkStats stats = statsRepository.findByAuditGroupIdAndDeletedFalse(auditGroupId)
                .orElseGet(() -> createStats(auditGroupId));

        // 这里简化处理，实际应该根据具体操作类型更新统计
        // 比如分配任务时增加pendingCount，完成任务时减少等
        statsRepository.save(stats);
    }

    private AuditGroupWorkStats createStats(String auditGroupId) {
        AuditGroup auditGroup = auditGroupRepository.findById(auditGroupId)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (auditGroup.getStatus() != AuditGroup.AuditGroupStatus.ACTIVE) {
            throw new BusinessException("审核组未启用");
        }

        AuditGroupWorkStats stats = new AuditGroupWorkStats();
        stats.setAuditGroupId(auditGroupId);
        stats.setTotalAssigned(0);
        stats.setTotalCompleted(0);
        stats.setTotalCancelled(0);
        stats.setPendingCount(0);
        stats.setInProgressCount(0);

        return statsRepository.save(stats);
    }

    private WorkStatsResponse convertToResponse(AuditGroupWorkStats stats) {
        WorkStatsResponse response = new WorkStatsResponse();
        response.setId(stats.getId());
        response.setAuditGroupId(stats.getAuditGroupId());
        response.setTotalAssigned(stats.getTotalAssigned());
        response.setTotalCompleted(stats.getTotalCompleted());
        response.setTotalCancelled(stats.getTotalCancelled());
        response.setPendingCount(stats.getPendingCount());
        response.setInProgressCount(stats.getInProgressCount());
        response.setCurrentTaskCount(stats.getCurrentTaskCount());
        response.setAvgProcessTime(stats.getAvgProcessTime());
        response.setLastTaskTime(stats.getLastTaskTime());
        response.setCreateTime(stats.getCreateTime());
        response.setUpdateTime(stats.getUpdateTime());

        // 加载审核组信息
        auditGroupRepository.findById(stats.getAuditGroupId())
                .ifPresent(group -> {
                    response.setAuditGroupName(group.getGroupName());
                    response.setAuditGroupCode(group.getGroupCode());
                });

        return response;
    }
}
