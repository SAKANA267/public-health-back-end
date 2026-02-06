package com.publichealth.public_health_api.module.reportcard.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import com.publichealth.public_health_api.module.reportcard.dto.*;
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
 */
@Slf4j
@RestController
@RequestMapping("/api/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建报告卡
     * POST /api/report-cards
     */
    @PostMapping
    @OperationLog(module = "报告卡管理", operationType = OperationType.CREATE, description = "创建报告卡")
    public ApiResponse<ReportCardDTO> createReportCard(@Valid @RequestBody CreateReportCardRequest request) {
        log.info("收到创建报告卡请求: inpatientNo={}, name={}", request.getInpatientNo(), request.getName());
        ReportCardDTO dto = reportCardService.createReportCard(request);
        return ApiResponse.success("报告卡创建成功", dto);
    }

    /**
     * 根据ID获取报告卡
     * GET /api/report-cards/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<ReportCardDTO> getReportCardById(@PathVariable String id) {
        log.info("获取报告卡: id={}", id);
        ReportCardDTO dto = reportCardService.getReportCardById(id);
        return ApiResponse.success(dto);
    }

    /**
     * 根据住院号获取报告卡
     * GET /api/report-cards/inpatient/{inpatientNo}
     */
    @GetMapping("/inpatient/{inpatientNo}")
    public ApiResponse<ReportCardDTO> getReportCardByInpatientNo(@PathVariable String inpatientNo) {
        log.info("根据住院号获取报告卡: inpatientNo={}", inpatientNo);
        ReportCardDTO dto = reportCardService.getReportCardByInpatientNo(inpatientNo);
        return ApiResponse.success(dto);
    }

    /**
     * 更新报告卡
     * 注意: 仅允许更新待审核状态的报告卡
     * PUT /api/report-cards/{id}
     */
    @PutMapping("/{id}")
    @OperationLog(module = "报告卡管理", operationType = OperationType.UPDATE, description = "更新报告卡")
    public ApiResponse<ReportCardDTO> updateReportCard(
            @PathVariable String id,
            @Valid @RequestBody UpdateReportCardRequest request) {
        log.info("更新报告卡: id={}", id);
        ReportCardDTO dto = reportCardService.updateReportCard(id, request);
        return ApiResponse.success("报告卡更新成功", dto);
    }

    /**
     * 删除报告卡
     * DELETE /api/report-cards/{id}
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "报告卡管理", operationType = OperationType.DELETE, description = "删除报告卡")
    public ApiResponse<Void> deleteReportCard(@PathVariable String id) {
        log.info("删除报告卡: id={}", id);
        reportCardService.deleteReportCard(id);
        return ApiResponse.success("报告卡已删除");
    }

    /**
     * 批量删除报告卡
     * DELETE /api/report-cards/batch
     */
    @DeleteMapping("/batch")
    @OperationLog(module = "报告卡管理", operationType = OperationType.DELETE, description = "批量删除报告卡")
    public ApiResponse<Void> batchDeleteReportCards(@RequestBody List<String> ids) {
        log.info("批量删除报告卡: count={}", ids.size());
        reportCardService.batchDeleteReportCards(ids);
        return ApiResponse.success("批量删除成功");
    }

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询报告卡列表
     * GET /api/report-cards?page=1&size=10&status=PENDING&hospitalArea=xxx
     */
    @GetMapping
    public ApiResponse<PageResult<ReportCardDTO>> getReportCardList(ReportCardQueryRequest request) {
        log.info("查询报告卡列表: {}", request);
        PageResult<ReportCardDTO> result = reportCardService.getReportCardList(request);
        return ApiResponse.success(result);
    }

    /**
     * 搜索报告卡
     * GET /api/report-cards/search?keyword=xxx
     */
    @GetMapping("/search")
    public ApiResponse<List<ReportCardDTO>> searchReportCards(@RequestParam String keyword) {
        log.info("搜索报告卡: keyword={}", keyword);
        List<ReportCardDTO> list = reportCardService.searchReportCards(keyword);
        return ApiResponse.success(list);
    }

    /**
     * 根据状态获取报告卡列表
     * GET /api/report-cards/status/{status}
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByStatus(@PathVariable ReportCard.ReportStatus status) {
        log.info("获取状态报告卡列表: status={}", status);
        List<ReportCardDTO> list = reportCardService.getReportCardsByStatus(status);
        return ApiResponse.success(list);
    }

    /**
     * 根据院区获取报告卡列表
     * GET /api/report-cards/hospital-area/{hospitalArea}
     */
    @GetMapping("/hospital-area/{hospitalArea}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByHospitalArea(@PathVariable String hospitalArea) {
        log.info("获取院区报告卡列表: hospitalArea={}", hospitalArea);
        List<ReportCardDTO> list = reportCardService.getReportCardsByHospitalArea(hospitalArea);
        return ApiResponse.success(list);
    }

    /**
     * 根据科室获取报告卡列表
     * GET /api/report-cards/department/{department}
     */
    @GetMapping("/department/{department}")
    public ApiResponse<List<ReportCardDTO>> getReportCardsByDepartment(@PathVariable String department) {
        log.info("获取科室报告卡列表: department={}", department);
        List<ReportCardDTO> list = reportCardService.getReportCardsByDepartment(department);
        return ApiResponse.success(list);
    }

    // ============================================
    // 审核操作
    // ============================================

    /**
     * 审核通过
     * PUT /api/report-cards/{id}/approve
     */
    @PutMapping("/{id}/approve")
    @OperationLog(module = "报告卡管理", operationType = OperationType.AUDIT, description = "审核通过")
    public ApiResponse<Void> approveReportCard(
            @PathVariable String id,
            @Valid @RequestBody AuditRequest request) {
        log.info("审核通过报告卡: id={}, auditorId={}", id, request.getAuditorId());
        reportCardService.approveReportCard(id, request.getAuditorId(), request.getRemark());
        return ApiResponse.success("审核通过");
    }

    /**
     * 审核拒绝
     * PUT /api/report-cards/{id}/reject
     */
    @PutMapping("/{id}/reject")
    @OperationLog(module = "报告卡管理", operationType = OperationType.AUDIT, description = "审核拒绝")
    public ApiResponse<Void> rejectReportCard(
            @PathVariable String id,
            @Valid @RequestBody AuditRequest request) {
        log.info("审核拒绝报告卡: id={}, auditorId={}", id, request.getAuditorId());
        reportCardService.rejectReportCard(id, request.getAuditorId(), request.getRemark());
        return ApiResponse.success("审核拒绝");
    }

    /**
     * 撤回审核
     * PUT /api/report-cards/{id}/withdraw
     */
    @PutMapping("/{id}/withdraw")
    public ApiResponse<Void> withdrawAudit(@PathVariable String id) {
        log.info("撤回审核: id={}", id);
        reportCardService.withdrawAudit(id);
        return ApiResponse.success("审核已撤回");
    }

    /**
     * 获取待审核报告卡列表
     * GET /api/report-cards/pending?page=1&size=10
     */
    @GetMapping("/pending")
    public ApiResponse<PageResult<ReportCardDTO>> getPendingCards(ReportCardQueryRequest request) {
        log.info("查询待审核报告卡列表: page={}, size={}", request.getPage(), request.getSize());
        PageResult<ReportCardDTO> result = reportCardService.getPendingCards(request);
        return ApiResponse.success(result);
    }

    /**
     * 获取我审核的报告卡列表
     * GET /api/report-cards/my-audited?auditorId=xxx
     */
    @GetMapping("/my-audited")
    public ApiResponse<List<ReportCardDTO>> getMyAuditedCards(@RequestParam String auditorId) {
        log.info("获取我审核的报告卡列表: auditorId={}", auditorId);
        List<ReportCardDTO> list = reportCardService.getMyAuditedCards(auditorId);
        return ApiResponse.success(list);
    }

    // ============================================
    // 统计查询
    // ============================================

    /**
     * 获取各状态统计数量
     * GET /api/report-cards/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Long>> getStatusStatistics() {
        log.info("获取报告卡状态统计");
        Map<String, Long> statistics = reportCardService.getStatusStatistics();
        return ApiResponse.success(statistics);
    }

    /**
     * 获取指定状态的数量
     * GET /api/report-cards/count?status=xxx
     */
    @GetMapping("/count")
    public ApiResponse<Long> getCountByStatus(@RequestParam ReportCard.ReportStatus status) {
        log.info("获取状态数量统计: status={}", status);
        Long count = reportCardService.getCountByStatus(status);
        return ApiResponse.success(count);
    }

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查住院号是否存在
     * GET /api/report-cards/check/inpatient-no?inpatientNo=xxx
     */
    @GetMapping("/check/inpatient-no")
    public ApiResponse<Boolean> checkInpatientNoExists(@RequestParam String inpatientNo) {
        boolean exists = reportCardService.existsByInpatientNo(inpatientNo);
        return ApiResponse.success(exists);
    }

    /**
     * 检查门诊号是否存在
     * GET /api/report-cards/check/outpatient-no?outpatientNo=xxx
     */
    @GetMapping("/check/outpatient-no")
    public ApiResponse<Boolean> checkOutpatientNoExists(@RequestParam String outpatientNo) {
        boolean exists = reportCardService.existsByOutpatientNo(outpatientNo);
        return ApiResponse.success(exists);
    }
}
