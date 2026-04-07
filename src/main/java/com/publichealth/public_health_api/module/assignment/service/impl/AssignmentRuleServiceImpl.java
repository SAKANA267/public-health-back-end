package com.publichealth.public_health_api.module.assignment.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleRequest;
import com.publichealth.public_health_api.module.assignment.dto.AssignmentRuleResponse;
import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule;
import com.publichealth.public_health_api.module.assignment.repository.AssignmentRuleRepository;
import com.publichealth.public_health_api.module.assignment.service.AssignmentRuleService;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分配规则Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentRuleServiceImpl implements AssignmentRuleService {

    private final AssignmentRuleRepository ruleRepository;
    private final AuditGroupRepository auditGroupRepository;

    @Override
    @Transactional
    public AssignmentRuleResponse createRule(AssignmentRuleRequest request) {
        // 检查规则编码是否已存在
        if (ruleRepository.findByRuleCodeAndDeletedFalse(request.getRuleCode()).isPresent()) {
            throw new BusinessException("规则编码已存在");
        }

        // 验证目标审核组存在
        if (request.getTargetGroupId() != null) {
            auditGroupRepository.findById(request.getTargetGroupId())
                    .orElseThrow(() -> new BusinessException("目标审核组不存在"));
        }

        AssignmentRule rule = new AssignmentRule();
        rule.setRuleName(request.getRuleName());
        rule.setRuleCode(request.getRuleCode());
        rule.setDiseaseCategory(request.getDiseaseCategory());
        rule.setHospitalArea(request.getHospitalArea());
        rule.setDepartment(request.getDepartment());
        rule.setAssignStrategy(request.getAssignStrategy());
        rule.setTargetGroupId(request.getTargetGroupId());
        rule.setPriority(request.getPriority());
        rule.setDeadlineHours(request.getDeadlineHours());
        rule.setStatus(request.getStatus());
        rule.setRuleOrder(request.getRuleOrder());

        ruleRepository.save(rule);
        return convertToResponse(rule);
    }

    @Override
    @Transactional
    public AssignmentRuleResponse updateRule(String ruleId, AssignmentRuleRequest request) {
        AssignmentRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException("规则不存在"));

        // 检查规则编码是否与其他规则冲突
        ruleRepository.findByRuleCodeAndDeletedFalse(request.getRuleCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(ruleId)) {
                        throw new BusinessException("规则编码已被使用");
                    }
                });

        // 验证目标审核组存在
        if (request.getTargetGroupId() != null) {
            auditGroupRepository.findById(request.getTargetGroupId())
                    .orElseThrow(() -> new BusinessException("目标审核组不存在"));
        }

        rule.setRuleName(request.getRuleName());
        rule.setRuleCode(request.getRuleCode());
        rule.setDiseaseCategory(request.getDiseaseCategory());
        rule.setHospitalArea(request.getHospitalArea());
        rule.setDepartment(request.getDepartment());
        rule.setAssignStrategy(request.getAssignStrategy());
        rule.setTargetGroupId(request.getTargetGroupId());
        rule.setPriority(request.getPriority());
        rule.setDeadlineHours(request.getDeadlineHours());
        rule.setStatus(request.getStatus());
        rule.setRuleOrder(request.getRuleOrder());

        ruleRepository.save(rule);
        return convertToResponse(rule);
    }

    @Override
    @Transactional
    public void deleteRule(String ruleId) {
        AssignmentRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException("规则不存在"));

        rule.setDeleted(true);
        ruleRepository.save(rule);
    }

    @Override
    public AssignmentRuleResponse getRuleDetail(String ruleId) {
        AssignmentRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException("规则不存在"));
        return convertToResponse(rule);
    }

    @Override
    public PageResult<AssignmentRuleResponse> getAllRules(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AssignmentRule> rulePage = ruleRepository.findAll(pageable);

        List<AssignmentRuleResponse> responses = rulePage.getContent().stream()
                .filter(rule -> !rule.getDeleted())
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResult.of(page, size, rulePage.getTotalElements(), responses);
    }

    @Override
    public List<AssignmentRuleResponse> getActiveRules() {
        List<AssignmentRule> rules = ruleRepository
                .findByDeletedFalseAndStatusOrderByRuleOrderAsc(AssignmentRule.RuleStatus.ACTIVE);

        return rules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentRuleResponse toggleRuleStatus(String ruleId) {
        AssignmentRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException("规则不存在"));

        if (rule.getStatus() == AssignmentRule.RuleStatus.ACTIVE) {
            rule.setStatus(AssignmentRule.RuleStatus.INACTIVE);
        } else {
            rule.setStatus(AssignmentRule.RuleStatus.ACTIVE);
        }

        ruleRepository.save(rule);
        return convertToResponse(rule);
    }

    private AssignmentRuleResponse convertToResponse(AssignmentRule rule) {
        AssignmentRuleResponse response = new AssignmentRuleResponse();
        response.setId(rule.getId());
        response.setRuleName(rule.getRuleName());
        response.setRuleCode(rule.getRuleCode());
        response.setDiseaseCategory(rule.getDiseaseCategory());
        response.setHospitalArea(rule.getHospitalArea());
        response.setDepartment(rule.getDepartment());
        response.setAssignStrategy(rule.getAssignStrategy());
        response.setAssignStrategyDescription(rule.getAssignStrategy().getDescription());
        response.setTargetGroupId(rule.getTargetGroupId());
        response.setPriority(rule.getPriority());
        response.setPriorityDescription(rule.getPriority().getDescription());
        response.setDeadlineHours(rule.getDeadlineHours());
        response.setStatus(rule.getStatus());
        response.setStatusDescription(rule.getStatus().getDescription());
        response.setRuleOrder(rule.getRuleOrder());
        response.setCreateTime(rule.getCreateTime());
        response.setUpdateTime(rule.getUpdateTime());

        // 加载审核组名称
        if (rule.getTargetGroupId() != null) {
            auditGroupRepository.findById(rule.getTargetGroupId())
                    .ifPresent(group -> response.setTargetGroupName(group.getGroupName()));
        }

        return response;
    }
}
