package com.publichealth.public_health_api.module.reportcard.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import com.publichealth.public_health_api.module.reportcard.dto.*;
import com.publichealth.public_health_api.module.reportcard.dto.statistics.*;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.service.ReportCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 传染病报告卡控制器
 * 处理报告卡相关的HTTP请求
 * 迁移说明：支持4张表的关联操作
 */
@Slf4j
@RestController
@RequestMapping("/api/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    @PostMapping
    @OperationLog(module = "报告卡管理", operationType = OperationType.CREATE, description = "创建报告卡")
    public ApiResponse<ReportCardDTO> createReportCard(@Valid @RequestBody CreateReportCardRequest request) {
        log.info("收到创建报告卡请求: inpatientNo={}, name={}",
                request.getInpatientNo(), request.getPatientInfo().getPatientName());
        ReportCardDTO dto = reportCardService.createReportCard(request);
        return ApiResponse.success("报告卡创建成功", dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportCardDTO> getReportCardById(@PathVariable String id) {
        log.info("获取报告卡: id={}", id);
        ReportCardDTO dto = reportCardService.getReportCardById(id);
        return ApiResponse.success(dto);
    }

    @GetMapping("/inpatient/{inpatientNo}")
    public ApiResponse<ReportCardDTO> getReportCardByInpatientNo(@PathVariable String inpatientNo) {
        log.info("根据住院号获取报告卡: inpatientNo={}", inpatientNo);
        ReportCardDTO dto = reportCardService.getReportCardByInpatientNo(inpatientNo);
        return ApiResponse.success(dto);
    }

    @PutMapping("/{id}")
    @OperationLog(module = "报告卡管理", operationType = OperationType.UPDATE, description = "更新报告卡")
    public ApiResponse<ReportCardDTO> updateReportCard(
            @PathVariable String id,
            @Valid @RequestBody UpdateReportCardRequest request) {
        log.info("更新报告卡: id={}", id);
        ReportCardDTO dto = reportCardService.updateReportCard(id, request);
        return ApiResponse.success("报告卡更新成功", dto);
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "报告卡管理", operationType = OperationType.DELETE, description = "删除报告卡")
    public ApiResponse<Void> deleteReportCard(@PathVariable String id) {
        log.info("删除报告卡: id={}", id);
        reportCardService.deleteReportCard(id);
        return ApiResponse.success("报告卡已删除");
    }

    @DeleteMapping("/batch")
    @OperationLog(module = "报告卡管理", operationType = OperationType.DELETE, description = "批量删除报告卡")
    public ApiResponse<Void> batchDeleteReportCards(@RequestBody List<String> ids) {
        log.info("批量删除报告卡: count={}", ids.size());
        reportCardService.batchDeleteReportCards(ids);
        return ApiResponse.success("批量删除成功");
    }

    @GetMapping
    public ApiResponse<PageResult<ReportCardDTO>> getReportCardList(
            ReportCardQueryRequest request,
            @RequestAttribute(required = false) String userId) {
        log.info("查询报告卡列表: {}", request);
        PageResult<ReportCardDTO> result = reportCardService.getReportCardList(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<ReportCardDTO>> getMyAccessibleReportCards(
            ReportCardQueryRequest request,
            @RequestAttribute String userId) {
        log.info("查询我的权限组可访问报告卡列表: userId={}, request={}", userId, request);
        PageResult<ReportCardDTO> result = reportCardService.getMyAccessibleReportCards(request, userId);
        return ApiResponse.success(result);
    }

    @GetMapping("/search")
    public ApiResponse<List<ReportCardDTO>> searchReportCards(
            @RequestParam String keyword,
            @RequestAttribute(required = false) String userId) {
        log.info("搜索报告卡: keyword={}", keyword);
        List<ReportCardDTO> list = reportCardService.searchReportCards(keyword);
        return ApiResponse.success(list);
    }

    @GetMapping("/my/search")
    public ApiResponse<List<ReportCardDTO>> searchMyAccessibleReportCards(
            @RequestParam String keyword,
            @RequestAttribute String userId) {
        log.info("搜索我的权限组可访问报告卡: userId={}, keyword={}", userId, keyword);
        List<ReportCardDTO> list = reportCardService.searchMyAccessibleReportCards(keyword, userId);
        return ApiResponse.success(list);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByStatus(@PathVariable ReportCard.AuditStatus status) {
        log.info("获取状态报告卡列表: status={}", status);
        List<ReportCardDTO> list = reportCardService.getReportCardsByStatus(status);
        return ApiResponse.success(list);
    }

    @GetMapping("/hospital-area/{hospitalArea}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByHospitalArea(@PathVariable String hospitalArea) {
        log.info("获取院区报告卡列表: hospitalArea={}", hospitalArea);
        List<ReportCardDTO> list = reportCardService.getReportCardsByHospitalArea(hospitalArea);
        return ApiResponse.success(list);
    }

    @GetMapping("/department/{department}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByDepartment(@PathVariable String department) {
        log.info("获取科室报告卡列表: department={}", department);
        List<ReportCardDTO> list = reportCardService.getReportCardsByDepartment(department);
        return ApiResponse.success(list);
    }

    @GetMapping("/unassigned")
    public ApiResponse<PageResult<ReportCardDTO>> getUnassignedReportCards(ReportCardQueryRequest request) {
        log.info("查询未分配报告卡列表: {}", request);
        PageResult<ReportCardDTO> result = reportCardService.getUnassignedReportCards(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/assign-status/{assignStatus}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByAssignStatus(
            @PathVariable com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit.AssignStatus assignStatus) {
        log.info("获取分配状态报告卡列表: assignStatus={}", assignStatus);
        List<ReportCardDTO> list = reportCardService.getReportCardsByAssignStatus(assignStatus);
        return ApiResponse.success(list);
    }

    @PutMapping("/{id}/approve")
    @OperationLog(module = "报告卡管理", operationType = OperationType.AUDIT, description = "审核通过")
    public ApiResponse<Void> approveReportCard(
            @PathVariable String id,
            @Valid @RequestBody AuditRequest request) {
        log.info("审核通过报告卡: id={}, auditorId={}", id, request.getAuditorId());
        reportCardService.approveReportCard(id, request.getAuditorId(), request.getRemark());
        return ApiResponse.success("审核通过");
    }

    @PutMapping("/{id}/reject")
    @OperationLog(module = "报告卡管理", operationType = OperationType.AUDIT, description = "审核拒绝")
    public ApiResponse<Void> rejectReportCard(
            @PathVariable String id,
            @Valid @RequestBody AuditRequest request) {
        log.info("审核拒绝报告卡: id={}, auditorId={}", id, request.getAuditorId());
        reportCardService.rejectReportCard(id, request.getAuditorId(), request.getRemark());
        return ApiResponse.success("审核拒绝");
    }

    @PutMapping("/{id}/withdraw")
    public ApiResponse<Void> withdrawAudit(@PathVariable String id) {
        log.info("撤回审核: id={}", id);
        reportCardService.withdrawAudit(id);
        return ApiResponse.success("审核已撤回");
    }

    @GetMapping("/pending")
    public ApiResponse<PageResult<ReportCardDTO>> getPendingCards(ReportCardQueryRequest request) {
        log.info("查询待审核报告卡列表: page={}, size={}", request.getPage(), request.getSize());
        PageResult<ReportCardDTO> result = reportCardService.getPendingCards(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/my-audited")
    public ApiResponse<List<ReportCardDTO>> getMyAuditedCards(@RequestParam String auditorId) {
        log.info("获取我审核的报告卡列表: auditorId={}", auditorId);
        List<ReportCardDTO> list = reportCardService.getMyAuditedCards(auditorId);
        return ApiResponse.success(list);
    }

    @GetMapping("/statistics")
    public ApiResponse<ReportCardStatisticsDTO> getStatistics() {
        log.info("获取报告卡统计数据");
        ReportCardStatisticsDTO statistics = reportCardService.getStatistics();
        return ApiResponse.success(statistics);
    }

    @GetMapping("/statistics/by-status")
    public ApiResponse<Map<String, Long>> getStatusStatistics() {
        log.info("获取报告卡状态统计");
        Map<String, Long> statistics = reportCardService.getStatusStatistics();
        return ApiResponse.success(statistics);
    }

    @GetMapping("/statistics/disease-distribution")
    public ApiResponse<List<DistributionItemDTO>> getDiseaseDistribution() {
        log.info("获取疾病种类分布统计");
        List<DistributionItemDTO> distribution = reportCardService.getDiseaseDistribution();
        return ApiResponse.success(distribution);
    }

    @GetMapping("/statistics/area-distribution")
    public ApiResponse<List<DistributionItemDTO>> getAreaDistribution() {
        log.info("获取院区分布统计");
        List<DistributionItemDTO> distribution = reportCardService.getAreaDistribution();
        return ApiResponse.success(distribution);
    }

    @GetMapping("/statistics/trend")
    public ApiResponse<List<TrendDataDTO>> getTrendData(@RequestParam(defaultValue = "week") String period) {
        log.info("获取时间趋势数据: period={}", period);
        List<TrendDataDTO> trend = reportCardService.getTrendData(period);
        return ApiResponse.success(trend);
    }

    @GetMapping("/statistics/recent-activities")
    public ApiResponse<List<RecentActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最近审核活动: limit={}", limit);
        List<RecentActivityDTO> activities = reportCardService.getRecentActivities(limit);
        return ApiResponse.success(activities);
    }

    @GetMapping("/count")
    public ApiResponse<Long> getCountByStatus(@RequestParam ReportCard.AuditStatus status) {
        log.info("获取状态数量统计: status={}", status);
        Long count = reportCardService.getCountByStatus(status);
        return ApiResponse.success(count);
    }

    @GetMapping("/check/inpatient-no")
    public ApiResponse<Boolean> checkInpatientNoExists(@RequestParam String inpatientNo) {
        boolean exists = reportCardService.existsByInpatientNo(inpatientNo);
        return ApiResponse.success(exists);
    }

    @GetMapping("/check/outpatient-no")
    public ApiResponse<Boolean> checkOutpatientNoExists(@RequestParam String outpatientNo) {
        boolean exists = reportCardService.existsByOutpatientNo(outpatientNo);
        return ApiResponse.success(exists);
    }
}
