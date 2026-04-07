package com.publichealth.public_health_api.module.assignment.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleRequest;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleResponse;
import com.publichealth.public_health_api.module.assignment.service.AssignmentRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分配规则Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/assignment-rules")
@RequiredArgsConstructor
public class AssignmentRuleController {

    private final AssignmentRuleService ruleService;

    /**
     * 创建分配规则
     */
    @PostMapping
    public ApiResponse<AssignmentRuleResponse> createRule(
            @Valid @RequestBody AssignmentRuleRequest request) {
        AssignmentRuleResponse response = ruleService.createRule(request);
        return ApiResponse.success(response);
    }

    /**
     * 更新分配规则
     */
    @PutMapping("/{ruleId}")
    public ApiResponse<AssignmentRuleResponse> updateRule(
            @PathVariable String ruleId,
            @Valid @RequestBody AssignmentRuleRequest request) {
        AssignmentRuleResponse response = ruleService.updateRule(ruleId, request);
        return ApiResponse.success(response);
    }

    /**
     * 删除分配规则
     */
    @DeleteMapping("/{ruleId}")
    public ApiResponse<Void> deleteRule(@PathVariable String ruleId) {
        ruleService.deleteRule(ruleId);
        return ApiResponse.success();
    }

    /**
     * 查询规则详情
     */
    @GetMapping("/{ruleId}")
    public ApiResponse<AssignmentRuleResponse> getRuleDetail(@PathVariable String ruleId) {
        AssignmentRuleResponse response = ruleService.getRuleDetail(ruleId);
        return ApiResponse.success(response);
    }

    /**
     * 查询所有规则
     */
    @GetMapping
    public ApiResponse<PageResult<AssignmentRuleResponse>> getAllRules(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AssignmentRuleResponse> response = ruleService.getAllRules(page, size);
        return ApiResponse.success(response);
    }

    /**
     * 查询启用的规则
     */
    @GetMapping("/active")
    public ApiResponse<List<AssignmentRuleResponse>> getActiveRules() {
        List<AssignmentRuleResponse> response = ruleService.getActiveRules();
        return ApiResponse.success(response);
    }

    /**
     * 启用/停用规则
     */
    @PostMapping("/{ruleId}/toggle")
    public ApiResponse<AssignmentRuleResponse> toggleRuleStatus(@PathVariable String ruleId) {
        AssignmentRuleResponse response = ruleService.toggleRuleStatus(ruleId);
        return ApiResponse.success(response);
    }
}
