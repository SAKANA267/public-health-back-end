package com.publichealth.public_health_api.module.cdcupload.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.cdcupload.dto.*;
import com.publichealth.public_health_api.module.cdcupload.service.CdcUploadService;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CDC上报控制器
 * 处理报告卡上报至国家疾控中心相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api/cdc-upload")
@RequiredArgsConstructor
public class CdcUploadController {

    private final CdcUploadService cdcUploadService;

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 获取已审核通过的报告卡列表（可上报列表）
     * GET /api/cdc-upload/approved
     */
    @GetMapping("/approved")
    public ApiResponse<PageResult<CdcUploadDTO>> getApprovedReportCards(CdcUploadPageRequest request) {
        log.info("查询可上报报告卡列表: page={}, size={}", request.getPage(), request.getSize());
        PageResult<CdcUploadDTO> result = cdcUploadService.getApprovedReportCards(request);
        return ApiResponse.success(result);
    }

    /**
     * 获取上报统计信息
     * GET /api/cdc-upload/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<CdcUploadStatistics> getUploadStatistics() {
        log.info("获取CDC上报统计信息");
        CdcUploadStatistics stats = cdcUploadService.getUploadStatistics();
        return ApiResponse.success(stats);
    }

    // ============================================
    // 上报操作
    // ============================================

    /**
     * 上报单个报告卡
     * POST /api/cdc-upload/upload/{reportCardId}
     */
    @PostMapping("/upload/{reportCardId}")
    @OperationLog(module = "CDC上报管理", operationType = OperationType.OTHER, description = "上报单个报告卡")
    public ApiResponse<Map<String, String>> uploadSingle(
            @PathVariable String reportCardId,
            @RequestBody Map<String, String> request) {
        String operatorId = request.get("operatorId");
        log.info("上报单个报告卡: reportCardId={}, operatorId={}", reportCardId, operatorId);
        cdcUploadService.uploadSingle(reportCardId, operatorId);
        return ApiResponse.success("上报请求已提交", Map.of("message", "上报请求已提交，正在处理中"));
    }

    /**
     * 批量上报报告卡
     * POST /api/cdc-upload/batch-upload
     */
    @PostMapping("/batch-upload")
    @OperationLog(module = "CDC上报管理", operationType = OperationType.OTHER, description = "批量上报报告卡")
    public ApiResponse<CdcUploadStatistics> batchUpload(@Valid @RequestBody CdcUploadRequest request) {
        log.info("批量上报报告卡: count={}", request.getReportCardIds().size());
        CdcUploadStatistics result = cdcUploadService.batchUpload(request);
        return ApiResponse.success("批量上报完成", result);
    }

    /**
     * 重试上报失败的报告卡
     * POST /api/cdc-upload/retry/{reportCardId}
     */
    @PostMapping("/retry/{reportCardId}")
    @OperationLog(module = "CDC上报管理", operationType = OperationType.OTHER, description = "重试上报失败报告卡")
    public ApiResponse<Map<String, String>> retryUpload(
            @PathVariable String reportCardId,
            @RequestBody Map<String, String> request) {
        String operatorId = request.get("operatorId");
        log.info("重试上报: reportCardId={}, operatorId={}", reportCardId, operatorId);
        cdcUploadService.retryUpload(reportCardId, operatorId);
        return ApiResponse.success("重试上报请求已提交", Map.of("message", "重试上报请求已提交，正在处理中"));
    }
}
