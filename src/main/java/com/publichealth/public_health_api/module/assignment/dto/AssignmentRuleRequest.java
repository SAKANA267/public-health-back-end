package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule.AssignStrategy;
import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule.RuleStatus;
import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分配规则请求DTO
 */
@Data
public class AssignmentRuleRequest {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 规则编码
     */
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    /**
     * 适用病种分类
     */
    private String diseaseCategory;

    /**
     * 适用院区
     */
    private String hospitalArea;

    /**
     * 适用科室
     */
    private String department;

    /**
     * 分配策略
     */
    @NotNull(message = "分配策略不能为空")
    private AssignStrategy assignStrategy;

    /**
     * 指定目标审核组ID
     */
    private String targetGroupId;

    /**
     * 默认优先级
     */
    private AssignmentPriority priority = AssignmentPriority.NORMAL;

    /**
     * 默认截止时长(小时)
     */
    private Integer deadlineHours;

    /**
     * 规则状态
     */
    private RuleStatus status = RuleStatus.ACTIVE;

    /**
     * 规则优先级顺序
     */
    private Integer ruleOrder = 0;
}
