package com.publichealth.public_health_api.module.assignment.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.module.assignment.dto.WorkStatsResponse;
import com.publichealth.public_health_api.module.assignment.service.WorkStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作统计Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/work-stats")
@RequiredArgsConstructor
public class WorkStatsController {

    private final WorkStatsService workStatsService;

    /**
     * 获取审核组工作统计
     */
    @GetMapping("/group/{auditGroupId}")
    public ApiResponse<WorkStatsResponse> getGroupStats(@PathVariable String auditGroupId) {
        WorkStatsResponse response = workStatsService.getGroupStats(auditGroupId);
        return ApiResponse.success(response);
    }

    /**
     * 获取所有审核组工作统计
     */
    @GetMapping("/all")
    public ApiResponse<List<WorkStatsResponse>> getAllGroupStats() {
        List<WorkStatsResponse> response = workStatsService.getAllGroupStats();
        return ApiResponse.success(response);
    }

    /**
     * 获取任务数最少的审核组ID
     */
    @GetMapping("/least-loaded")
    public ApiResponse<String> getLeastLoadedGroupId() {
        String response = workStatsService.getLeastLoadedGroupId();
        return ApiResponse.success("查询成功", response);
    }
}
