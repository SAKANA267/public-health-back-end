package com.publichealth.public_health_api.module.assignment.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.assignment.dto.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报卡分配Service接口
 */
public interface ReportCardAssignmentService {

    /**
     * 手动分配报卡给审核组
     */
    AssignmentResponse assignToGroup(AssignmentRequest request, String operatorId);

    /**
     * 自动分配报卡（根据规则匹配）
     */
    AssignmentResponse autoAssign(AutoAssignRequest request);

    /**
     * 接受任务
     */
    AssignmentResponse acceptTask(String assignmentId, String operatorId);

    /**
     * 完成任务
     */
    AssignmentResponse completeTask(AssignmentStatusUpdateRequest request, String operatorId);

    /**
     * 取消任务
     */
    AssignmentResponse cancelTask(AssignmentStatusUpdateRequest request, String operatorId);

    /**
     * 重新分配任务
     */
    AssignmentResponse reassign(String assignmentId, String newAuditGroupId, String operatorId);

    /**
     * 查询任务详情
     */
    AssignmentResponse getAssignmentDetail(String assignmentId);

    /**
     * 查询审核组的任务列表
     */
    PageResult<AssignmentResponse> getGroupAssignments(String auditGroupId, Integer page, Integer size);

    /**
     * 查询待处理任务列表
     */
    PageResult<AssignmentResponse> getPendingAssignments(Integer page, Integer size);

    /**
     * 查询即将超时的任务
     */
    List<AssignmentResponse> getOverdueAssignments();

    /**
     * 查询指定报卡的分配记录
     */
    List<AssignmentResponse> getReportCardAssignments(String reportCardId);

    /**
     * 查询操作日志
     */
    PageResult<AssignmentOperationLogResponse> getOperationLogs(String assignmentId, Integer page, Integer size);
}
