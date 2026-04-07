package com.publichealth.public_health_api.module.assignment.dto;

import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule.AssignStrategy;
import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule.RuleStatus;
import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentPriority;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分配规则响应DTO
 */
@Data
public class AssignmentRuleResponse {

    private String id;
    private String ruleName;
    private String ruleCode;
    private String diseaseCategory;
    private String hospitalArea;
    private String department;
    private AssignStrategy assignStrategy;
    private String assignStrategyDescription;
    private String targetGroupId;
    private String targetGroupName;
    private AssignmentPriority priority;
    private String priorityDescription;
    private Integer deadlineHours;
    private RuleStatus status;
    private String statusDescription;
    private Integer ruleOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
