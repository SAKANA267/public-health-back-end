package com.publichealth.public_health_api.module.assignment.service.impl;

import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.assignment.dto.WorkStatsResponse;
import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentStatus;
import com.publichealth.public_health_api.module.assignment.repository.ReportCardAssignmentRepository;
import com.publichealth.public_health_api.module.assignment.service.WorkStatsService;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作统计Service实现 - 重构版
 * 统计数据改为实时查询，不再依赖 audit_group_work_stats 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkStatsServiceImpl implements WorkStatsService {

    private final ReportCardAssignmentRepository assignmentRepository;
    private final AuditGroupRepository auditGroupRepository;

    @Override
    public WorkStatsResponse getGroupStats(String auditGroupId) {
        AuditGroup auditGroup = auditGroupRepository.findById(auditGroupId)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (auditGroup.getStatus() != AuditGroup.AuditGroupStatus.ACTIVE) {
            throw new BusinessException("审核组未启用");
        }

        return buildStatsFromQuery(auditGroup);
    }

    @Override
    public List<WorkStatsResponse> getAllGroupStats() {
        List<AuditGroup> auditGroups = auditGroupRepository.findByStatusAndDeletedFalse(
                AuditGroup.AuditGroupStatus.ACTIVE);

        return auditGroups.stream()
                .map(this::buildStatsFromQuery)
                .sorted(Comparator.comparing(WorkStatsResponse::getCurrentTaskCount))
                .collect(Collectors.toList());
    }

    @Override
    public String getLeastLoadedGroupId() {
        List<AuditGroup> auditGroups = auditGroupRepository.findByStatusAndDeletedFalse(
                AuditGroup.AuditGroupStatus.ACTIVE);

        return auditGroups.stream()
                .min(Comparator.comparing(g -> {
                    long pending = safeCount(assignmentRepository
                            .countByAuditGroupIdAndStatusAndDeletedFalse(g.getId(), AssignmentStatus.PENDING));
                    long inProgress = safeCount(assignmentRepository
                            .countByAuditGroupIdAndStatusAndDeletedFalse(g.getId(), AssignmentStatus.IN_PROGRESS));
                    return pending + inProgress;
                }))
                .map(AuditGroup::getId)
                .orElse(null);
    }

    @Override
    @Deprecated
    public void updateStats(String auditGroupId) {
        log.debug("updateStats 方法已弃用，统计数据改为实时查询");
    }

    private WorkStatsResponse buildStatsFromQuery(AuditGroup group) {
        String groupId = group.getId();

        long total = safeCount(assignmentRepository.countByAuditGroupIdAndDeletedFalse(groupId));
        long completed = safeCount(assignmentRepository.countByAuditGroupIdAndStatusAndDeletedFalse(groupId, AssignmentStatus.COMPLETED));
        long cancelled = safeCount(assignmentRepository.countByAuditGroupIdAndStatusAndDeletedFalse(groupId, AssignmentStatus.CANCELLED));
        long pending = safeCount(assignmentRepository.countByAuditGroupIdAndStatusAndDeletedFalse(groupId, AssignmentStatus.PENDING));
        long inProgress = safeCount(assignmentRepository.countByAuditGroupIdAndStatusAndDeletedFalse(groupId, AssignmentStatus.IN_PROGRESS));

        WorkStatsResponse response = new WorkStatsResponse();
        response.setAuditGroupId(groupId);
        response.setAuditGroupName(group.getGroupName());
        response.setAuditGroupCode(group.getGroupCode());

        response.setTotalAssigned((int) total);
        response.setTotalCompleted((int) completed);
        response.setTotalCancelled((int) cancelled);
        response.setPendingCount((int) pending);
        response.setInProgressCount((int) inProgress);
        response.setCurrentTaskCount((int) (pending + inProgress));

        Double avgTime = assignmentRepository.getAvgProcessTime(groupId);
        response.setAvgProcessTime(avgTime != null ? avgTime.intValue() : null);

        String lastTaskTimeStr = assignmentRepository.getLastTaskTime(groupId);
        if (lastTaskTimeStr != null && !lastTaskTimeStr.startsWith("1970")) {
            response.setLastTaskTime(LocalDateTime.parse(lastTaskTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return response;
    }

    private long safeCount(Long count) {
        return count != null ? count : 0L;
    }
}
