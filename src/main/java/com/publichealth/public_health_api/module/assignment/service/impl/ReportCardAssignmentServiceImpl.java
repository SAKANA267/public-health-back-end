package com.publichealth.public_health_api.module.assignment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.assignment.dto.*;
import com.publichealth.public_health_api.module.assignment.entity.*;
import com.publichealth.public_health_api.module.assignment.repository.*;
import com.publichealth.public_health_api.module.assignment.service.ReportCardAssignmentService;
import com.publichealth.public_health_api.module.assignment.service.WorkStatsService;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupRepository;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardRepository;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报卡分配Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportCardAssignmentServiceImpl implements ReportCardAssignmentService {

    private final ReportCardAssignmentRepository assignmentRepository;
    private final AuditGroupRepository auditGroupRepository;
    private final ReportCardRepository reportCardRepository;
    private final SysUserRepository sysUserRepository;
    private final AssignmentOperationLogRepository operationLogRepository;
    private final AssignmentRuleRepository ruleRepository;
    private final WorkStatsService workStatsService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AssignmentResponse assignToGroup(AssignmentRequest request, String operatorId) {
        // 验证报卡存在
        ReportCard reportCard = reportCardRepository.findById(request.getReportCardId())
                .orElseThrow(() -> new BusinessException("报卡不存在"));

        // 验证审核组存在且启用
        AuditGroup auditGroup = auditGroupRepository.findById(request.getAuditGroupId())
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (auditGroup.getStatus() != AuditGroup.AuditGroupStatus.ACTIVE) {
            throw new BusinessException("审核组未启用");
        }

        // 检查是否已有进行中的任务（排除已过期的任务）
        List<ReportCardAssignment> existingAssignments = assignmentRepository
                .findAllByReportCardIdAndDeletedFalseAndStatusNot(
                        request.getReportCardId(),
                        ReportCardAssignment.AssignmentStatus.COMPLETED);

        // 处理所有已存在的分配记录
        boolean hasUnexpiredTask = false;
        for (ReportCardAssignment existing : existingAssignments) {
            // 检查任务是否已过期
            boolean isExpired = existing.getDeadline() != null
                    && existing.getDeadline().isBefore(LocalDateTime.now());

            if (isExpired) {
                // 任务已过期，取消旧任务
                log.info("检测到过期任务，自动取消: assignmentId={}, reportCardId={}",
                        existing.getId(), request.getReportCardId());
                existing.setStatus(ReportCardAssignment.AssignmentStatus.CANCELLED);
                existing.setRejectReason("任务过期，系统自动取消");
                assignmentRepository.save(existing);
            } else {
                // 任务未过期，标记为有未过期任务
                hasUnexpiredTask = true;
            }
        }

        // 如果有未过期的任务，拒绝操作
        if (hasUnexpiredTask) {
            throw new BusinessException("该报卡已有进行中的任务");
        }

        // 如果取消了所有过期任务，更新报卡状态为未分配
        if (!existingAssignments.isEmpty()) {
            ReportCard card = reportCardRepository.findById(request.getReportCardId()).orElse(null);
            if (card != null) {
                card.setAssignStatus(ReportCard.AssignStatus.UNASSIGNED);
                reportCardRepository.save(card);
            }
        }

        // 创建分配记录
        ReportCardAssignment assignment = new ReportCardAssignment();
        assignment.setId(java.util.UUID.randomUUID().toString()); // 手动生成ID
        assignment.setReportCardId(request.getReportCardId());
        assignment.setAuditGroupId(request.getAuditGroupId());
        assignment.setAssignerId(operatorId);
        assignment.setPriority(request.getPriority());
        assignment.setDeadline(request.getDeadline());
        assignment.setRemark(request.getRemark());
        assignment.setStatus(ReportCardAssignment.AssignmentStatus.PENDING);

        assignmentRepository.save(assignment);

        // 更新报卡分配状态为已分配
        reportCard.setAssignStatus(ReportCard.AssignStatus.ASSIGNED);
        reportCardRepository.save(reportCard);

        // 更新统计
        workStatsService.updateStats(request.getAuditGroupId());

        // 记录日志
        recordOperationLog(assignment, AssignmentOperationLog.OperationType.ASSIGN,
                null, ReportCardAssignment.AssignmentStatus.PENDING.name(), operatorId);

        return convertToResponse(assignment, reportCard, auditGroup, null);
    }

    @Override
    @Transactional
    public AssignmentResponse autoAssign(AutoAssignRequest request) {
        // 查找匹配的规则
        List<AssignmentRule> matchingRules = ruleRepository.findMatchingRules(
                request.getDiseaseCategory(),
                request.getHospitalArea(),
                request.getDepartment());

        if (matchingRules.isEmpty()) {
            throw new BusinessException("未找到匹配的分配规则");
        }

        AssignmentRule rule = matchingRules.get(0);

        // 根据策略选择审核组
        String targetGroupId;
        switch (rule.getAssignStrategy()) {
            case MANUAL:
                targetGroupId = rule.getTargetGroupId();
                break;
            case LEAST_TASKS:
                targetGroupId = workStatsService.getLeastLoadedGroupId();
                break;
            case ROUND_ROBIN:
            case LEADER:
            default:
                // 轮询或组长分配暂时使用最少任务策略
                targetGroupId = workStatsService.getLeastLoadedGroupId();
                break;
        }

        if (targetGroupId == null) {
            throw new BusinessException("无可用的审核组");
        }

        // 构建分配请求
        AssignmentRequest assignRequest = new AssignmentRequest();
        assignRequest.setReportCardId(request.getReportCardId());
        assignRequest.setAuditGroupId(targetGroupId);
        assignRequest.setPriority(rule.getPriority());
        assignRequest.setRuleId(rule.getId());

        // 设置截止时间
        if (rule.getDeadlineHours() != null) {
            assignRequest.setDeadline(LocalDateTime.now().plusHours(rule.getDeadlineHours()));
        }

        return assignToGroup(assignRequest, request.getAssignerId());
    }

    @Override
    @Transactional
    public AssignmentResponse acceptTask(String assignmentId, String operatorId) {
        ReportCardAssignment assignment = getAssignmentEntity(assignmentId);

        if (assignment.getStatus() != ReportCardAssignment.AssignmentStatus.PENDING) {
            throw new BusinessException("任务状态不正确，无法接单");
        }

        // 验证操作人是否为审核组成员
        validateGroupMember(assignment.getAuditGroupId(), operatorId);

        ReportCardAssignment.AssignmentStatus oldStatus = assignment.getStatus();
        assignment.setStatus(ReportCardAssignment.AssignmentStatus.IN_PROGRESS);
        assignment.setAcceptTime(LocalDateTime.now());
        assignmentRepository.save(assignment);

        // 更新报卡分配状态为处理中
        ReportCard reportCard = reportCardRepository.findById(assignment.getReportCardId())
                .orElseThrow(() -> new BusinessException("报卡不存在"));
        reportCard.setAssignStatus(ReportCard.AssignStatus.IN_PROGRESS);
        reportCardRepository.save(reportCard);

        // 更新统计
        workStatsService.updateStats(assignment.getAuditGroupId());

        // 记录日志
        recordOperationLog(assignment, AssignmentOperationLog.OperationType.ACCEPT,
                oldStatus.name(), assignment.getStatus().name(), operatorId);

        return convertToResponse(assignment, null, null, null);
    }

    @Override
    @Transactional
    public AssignmentResponse completeTask(AssignmentStatusUpdateRequest request, String operatorId) {
        ReportCardAssignment assignment = getAssignmentEntity(request.getAssignmentId());

        if (assignment.getStatus() != ReportCardAssignment.AssignmentStatus.IN_PROGRESS) {
            throw new BusinessException("任务状态不正确，无法完成");
        }

        // 验证操作人是否为审核组成员
        validateGroupMember(assignment.getAuditGroupId(), operatorId);

        ReportCardAssignment.AssignmentStatus oldStatus = assignment.getStatus();
        assignment.setStatus(ReportCardAssignment.AssignmentStatus.COMPLETED);
        assignment.setCompleteTime(LocalDateTime.now());
        assignmentRepository.save(assignment);

        // 更新报卡状态
        ReportCard reportCard = reportCardRepository.findById(assignment.getReportCardId())
                .orElseThrow(() -> new BusinessException("报卡不存在"));
        reportCard.setAssignStatus(ReportCard.AssignStatus.COMPLETED);
        reportCard.setAuditStatus(ReportCard.ReportStatus.APPROVED);
        reportCard.setAuditorId(operatorId);
        reportCard.setAuditDate(LocalDateTime.now().toLocalDate());
        reportCardRepository.save(reportCard);

        // 更新统计
        workStatsService.updateStats(assignment.getAuditGroupId());

        // 记录日志
        recordOperationLog(assignment, AssignmentOperationLog.OperationType.COMPLETE,
                oldStatus.name(), assignment.getStatus().name(), operatorId);

        return convertToResponse(assignment, reportCard, null, null);
    }

    @Override
    @Transactional
    public AssignmentResponse cancelTask(AssignmentStatusUpdateRequest request, String operatorId) {
        ReportCardAssignment assignment = getAssignmentEntity(request.getAssignmentId());

        if (assignment.getStatus() == ReportCardAssignment.AssignmentStatus.COMPLETED) {
            throw new BusinessException("已完成的任务无法取消");
        }

        ReportCardAssignment.AssignmentStatus oldStatus = assignment.getStatus();
        assignment.setStatus(ReportCardAssignment.AssignmentStatus.CANCELLED);
        assignment.setRejectReason(request.getRemark());
        assignmentRepository.save(assignment);

        // 更新报卡分配状态为未分配
        ReportCard reportCard = reportCardRepository.findById(assignment.getReportCardId())
                .orElseThrow(() -> new BusinessException("报卡不存在"));
        reportCard.setAssignStatus(ReportCard.AssignStatus.UNASSIGNED);
        reportCardRepository.save(reportCard);

        // 更新统计
        workStatsService.updateStats(assignment.getAuditGroupId());

        // 记录日志
        recordOperationLog(assignment, AssignmentOperationLog.OperationType.CANCEL,
                oldStatus.name(), assignment.getStatus().name(), operatorId);

        return convertToResponse(assignment, null, null, null);
    }

    @Override
    @Transactional
    public AssignmentResponse reassign(String assignmentId, String newAuditGroupId, String operatorId) {
        ReportCardAssignment oldAssignment = getAssignmentEntity(assignmentId);

        // 取消旧任务
        AssignmentStatusUpdateRequest cancelRequest = new AssignmentStatusUpdateRequest();
        cancelRequest.setAssignmentId(assignmentId);
        cancelRequest.setStatus(ReportCardAssignment.AssignmentStatus.CANCELLED);
        cancelRequest.setRemark("重新分配");
        cancelRequest.setVersion(oldAssignment.getVersion());
        cancelTask(cancelRequest, operatorId);

        // 创建新任务
        AssignmentRequest newRequest = new AssignmentRequest();
        newRequest.setReportCardId(oldAssignment.getReportCardId());
        newRequest.setAuditGroupId(newAuditGroupId);
        newRequest.setPriority(oldAssignment.getPriority());
        newRequest.setDeadline(oldAssignment.getDeadline());
        newRequest.setRemark("重新分配：" + oldAssignment.getRemark());

        return assignToGroup(newRequest, operatorId);
    }

    @Override
    public AssignmentResponse getAssignmentDetail(String assignmentId) {
        ReportCardAssignment assignment = getAssignmentEntity(assignmentId);
        return convertToResponse(assignment, null, null, null);
    }

    @Override
    public PageResult<AssignmentResponse> getGroupAssignments(String auditGroupId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ReportCardAssignment> assignmentPage = assignmentRepository
                .findAll(pageable);

        List<AssignmentResponse> responses = assignmentPage.getContent().stream()
                .filter(a -> a.getAuditGroupId().equals(auditGroupId)
                        && !a.getDeleted()
                        && a.getStatus() == ReportCardAssignment.AssignmentStatus.PENDING)
                .map(a -> convertToResponse(a, null, null, null))
                .collect(Collectors.toList());

        return PageResult.of(page, size, (long) responses.size(), responses);
    }

    @Override
    public PageResult<AssignmentResponse> getPendingAssignments(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ReportCardAssignment> assignmentPage = assignmentRepository.findAll(pageable);

        List<AssignmentResponse> responses = assignmentPage.getContent().stream()
                .filter(a -> !a.getDeleted() && a.getStatus() == ReportCardAssignment.AssignmentStatus.PENDING)
                .map(a -> convertToResponse(a, null, null, null))
                .collect(Collectors.toList());

        return PageResult.of(page, size, (long) responses.size(), responses);
    }

    @Override
    public List<AssignmentResponse> getOverdueAssignments() {
        List<ReportCardAssignment> assignments = assignmentRepository.findOverdueAssignments(
                List.of(ReportCardAssignment.AssignmentStatus.PENDING,
                        ReportCardAssignment.AssignmentStatus.IN_PROGRESS),
                LocalDateTime.now());

        return assignments.stream()
                .map(a -> convertToResponse(a, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getReportCardAssignments(String reportCardId) {
        List<ReportCardAssignment> assignments = assignmentRepository.findAll().stream()
                .filter(a -> a.getReportCardId().equals(reportCardId) && !a.getDeleted())
                .collect(Collectors.toList());

        return assignments.stream()
                .map(a -> convertToResponse(a, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<AssignmentOperationLogResponse> getOperationLogs(String assignmentId, Integer page, Integer size) {
        List<AssignmentOperationLog> logs = operationLogRepository.findByAssignmentIdOrderByCreateTimeDesc(assignmentId);

        List<AssignmentOperationLogResponse> responses = logs.stream()
                .skip((page - 1) * size)
                .limit(size)
                .map(this::convertToLogResponse)
                .collect(Collectors.toList());

        return PageResult.of(page, size, (long) logs.size(), responses);
    }

    @Override
    public AssignmentStatisticsDTO getStatistics() {
        log.info("获取任务分配统计数据");

        AssignmentStatisticsDTO dto = new AssignmentStatisticsDTO();

        // 获取总数
        dto.setTotal(assignmentRepository.countByDeletedFalse());

        // 获取各状态数量
        dto.setPending(assignmentRepository.countByStatusAndDeletedFalse(
                ReportCardAssignment.AssignmentStatus.PENDING));
        dto.setInProgress(assignmentRepository.countByStatusAndDeletedFalse(
                ReportCardAssignment.AssignmentStatus.IN_PROGRESS));
        dto.setCompleted(assignmentRepository.countByStatusAndDeletedFalse(
                ReportCardAssignment.AssignmentStatus.COMPLETED));
        dto.setCancelled(assignmentRepository.countByStatusAndDeletedFalse(
                ReportCardAssignment.AssignmentStatus.CANCELLED));

        return dto;
    }

    // ============================================
    // 私有方法
    // ============================================

    private ReportCardAssignment getAssignmentEntity(String assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException("任务不存在"));
    }

    private void validateGroupMember(String auditGroupId, String userId) {
        // 简化验证，实际应该查询审核组成员表
        // 这里假设只要用户存在就可以操作
        sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private void recordOperationLog(ReportCardAssignment assignment,
                                    AssignmentOperationLog.OperationType operationType,
                                    String beforeStatus, String afterStatus, String operatorId) {
        AssignmentOperationLog log = new AssignmentOperationLog();
        log.setAssignmentId(assignment.getId());
        log.setReportCardId(assignment.getReportCardId());
        log.setOperationType(operationType);
        log.setOperatorId(operatorId);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);

        // 构建操作详情
        Map<String, Object> detail = new HashMap<>();
        detail.put("auditGroupId", assignment.getAuditGroupId());
        detail.put("priority", assignment.getPriority());
        detail.put("deadline", assignment.getDeadline());
        try {
            log.setOperationDetail(objectMapper.writeValueAsString(detail));
        } catch (JsonProcessingException e) {
            log.setOperationDetail("{}");
        }

        operationLogRepository.save(log);
    }

    private AssignmentResponse convertToResponse(ReportCardAssignment assignment,
                                                  ReportCard reportCard,
                                                  AuditGroup auditGroup,
                                                  SysUser assigner) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setReportCardId(assignment.getReportCardId());
        response.setAuditGroupId(assignment.getAuditGroupId());
        response.setStatus(assignment.getStatus());
        response.setStatusDescription(assignment.getStatus().getDescription());
        response.setAssignTime(assignment.getAssignTime());
        response.setDeadline(assignment.getDeadline());
        response.setAcceptTime(assignment.getAcceptTime());
        response.setCompleteTime(assignment.getCompleteTime());
        response.setPriority(assignment.getPriority());
        response.setPriorityDescription(assignment.getPriority().getDescription());
        response.setRemark(assignment.getRemark());
        response.setRejectReason(assignment.getRejectReason());
        response.setVersion(assignment.getVersion());
        response.setCreateTime(assignment.getCreateTime());
        response.setUpdateTime(assignment.getUpdateTime());

        // 加载关联数据
        if (reportCard == null) {
            reportCard = reportCardRepository.findById(assignment.getReportCardId()).orElse(null);
        }
        if (reportCard != null) {
            response.setReportCardInpatientNo(reportCard.getInpatientNo());
            response.setReportCardPatientName(reportCard.getName());
            response.setReportCardDiagnosisName(reportCard.getDiagnosisName());
        }

        if (auditGroup == null) {
            auditGroup = auditGroupRepository.findById(assignment.getAuditGroupId()).orElse(null);
        }
        if (auditGroup != null) {
            response.setAuditGroupName(auditGroup.getGroupName());
        }

        if (assigner == null) {
            assigner = sysUserRepository.findById(assignment.getAssignerId()).orElse(null);
        }
        if (assigner != null) {
            response.setAssignerId(assigner.getId());
            response.setAssignerName(assigner.getName());
        }

        return response;
    }

    private AssignmentOperationLogResponse convertToLogResponse(AssignmentOperationLog log) {
        AssignmentOperationLogResponse response = new AssignmentOperationLogResponse();
        response.setId(log.getId());
        response.setAssignmentId(log.getAssignmentId());
        response.setReportCardId(log.getReportCardId());
        response.setOperationType(log.getOperationType());
        response.setOperationTypeDescription(log.getOperationType().getDescription());
        response.setOperatorId(log.getOperatorId());
        response.setOperatorName(log.getOperatorName());
        response.setBeforeStatus(log.getBeforeStatus());
        response.setAfterStatus(log.getAfterStatus());
        response.setOperationDetail(log.getOperationDetail());
        response.setRemark(log.getRemark());
        response.setCreateTime(log.getCreateTime());
        return response;
    }
}
