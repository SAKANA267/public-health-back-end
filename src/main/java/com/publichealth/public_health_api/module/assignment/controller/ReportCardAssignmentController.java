package com.publichealth.public_health_api.module.assignment.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.assignment.dto.*;
import com.publichealth.public_health_api.module.assignment.service.ReportCardAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报卡分配Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class ReportCardAssignmentController {

    private final ReportCardAssignmentService assignmentService;

    /**
     * 手动分配报卡给审核组
     */
    @PostMapping("/assign")
    public ApiResponse<AssignmentResponse> assignToGroup(
            @Valid @RequestBody AssignmentRequest request,
            @RequestAttribute("userId") String userId) {
        AssignmentResponse response = assignmentService.assignToGroup(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 自动分配报卡
     */
    @PostMapping("/auto-assign")
    public ApiResponse<AssignmentResponse> autoAssign(
            @Valid @RequestBody AutoAssignRequest request) {
        AssignmentResponse response = assignmentService.autoAssign(request);
        return ApiResponse.success(response);
    }

    /**
     * 接受任务
     */
    @PostMapping("/{assignmentId}/accept")
    public ApiResponse<AssignmentResponse> acceptTask(
            @PathVariable String assignmentId,
            @RequestAttribute("userId") String userId) {
        AssignmentResponse response = assignmentService.acceptTask(assignmentId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 完成任务
     */
    @PostMapping("/complete")
    public ApiResponse<AssignmentResponse> completeTask(
            @Valid @RequestBody AssignmentStatusUpdateRequest request,
            @RequestAttribute("userId") String userId) {
        AssignmentResponse response = assignmentService.completeTask(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 取消任务
     */
    @PostMapping("/cancel")
    public ApiResponse<AssignmentResponse> cancelTask(
            @Valid @RequestBody AssignmentStatusUpdateRequest request,
            @RequestAttribute("userId") String userId) {
        AssignmentResponse response = assignmentService.cancelTask(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 重新分配任务
     */
    @PostMapping("/{assignmentId}/reassign")
    public ApiResponse<AssignmentResponse> reassign(
            @PathVariable String assignmentId,
            @RequestParam String newAuditGroupId,
            @RequestAttribute("userId") String userId) {
        AssignmentResponse response = assignmentService.reassign(assignmentId, newAuditGroupId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/{assignmentId}")
    public ApiResponse<AssignmentResponse> getAssignmentDetail(@PathVariable String assignmentId) {
        AssignmentResponse response = assignmentService.getAssignmentDetail(assignmentId);
        return ApiResponse.success(response);
    }

    /**
     * 查询审核组的任务列表
     */
    @GetMapping("/group/{auditGroupId}")
    public ApiResponse<PageResult<AssignmentResponse>> getGroupAssignments(
            @PathVariable String auditGroupId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AssignmentResponse> response = assignmentService.getGroupAssignments(auditGroupId, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 查询待处理任务列表
     */
    @GetMapping("/pending")
    public ApiResponse<PageResult<AssignmentResponse>> getPendingAssignments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AssignmentResponse> response = assignmentService.getPendingAssignments(page, size);
        return ApiResponse.success(response);
    }

    /**
     * 查询即将超时的任务
     */
    @GetMapping("/overdue")
    public ApiResponse<List<AssignmentResponse>> getOverdueAssignments() {
        List<AssignmentResponse> response = assignmentService.getOverdueAssignments();
        return ApiResponse.success(response);
    }

    /**
     * 查询指定报卡的分配记录
     */
    @GetMapping("/report-card/{reportCardId}")
    public ApiResponse<List<AssignmentResponse>> getReportCardAssignments(
            @PathVariable String reportCardId) {
        List<AssignmentResponse> response = assignmentService.getReportCardAssignments(reportCardId);
        return ApiResponse.success(response);
    }

    /**
     * 查询操作日志
     */
    @GetMapping("/{assignmentId}/logs")
    public ApiResponse<PageResult<AssignmentOperationLogResponse>> getOperationLogs(
            @PathVariable String assignmentId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AssignmentOperationLogResponse> response = assignmentService.getOperationLogs(assignmentId, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 获取任务分配统计
     * GET /api/assignments/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<AssignmentStatisticsDTO> getStatistics() {
        log.info("获取任务分配统计");
        AssignmentStatisticsDTO statistics = assignmentService.getStatistics();
        return ApiResponse.success(statistics);
    }
}
