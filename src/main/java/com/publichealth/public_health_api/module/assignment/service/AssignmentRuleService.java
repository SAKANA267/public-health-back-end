package com.publichealth.public_health_api.module.assignment.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleRequest;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleResponse;

import java.util.List;

/**
 * 分配规则Service接口
 */
public interface AssignmentRuleService {

    /**
     * 创建分配规则
     */
    AssignmentRuleResponse createRule(AssignmentRuleRequest request);

    /**
     * 更新分配规则
     */
    AssignmentRuleResponse updateRule(String ruleId, AssignmentRuleRequest request);

    /**
     * 删除分配规则
     */
    void deleteRule(String ruleId);

    /**
     * 查询规则详情
     */
    AssignmentRuleResponse getRuleDetail(String ruleId);

    /**
     * 查询所有规则
     */
    PageResult<AssignmentRuleResponse> getAllRules(Integer page, Integer size);

    /**
     * 查询启用的规则
     */
    List<AssignmentRuleResponse> getActiveRules();

    /**
     * 启用/停用规则
     */
    AssignmentRuleResponse toggleRuleStatus(String ruleId);
}
