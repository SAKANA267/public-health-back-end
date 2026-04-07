package com.publichealth.public_health_api.module.assignment.repository;

import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule;
import com.publichealth.public_health_api.module.assignment.entity.AssignmentRule.RuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 任务分配规则Repository
 */
@Repository
public interface AssignmentRuleRepository extends JpaRepository<AssignmentRule, String> {

    /**
     * 根据规则编码查询
     */
    Optional<AssignmentRule> findByRuleCodeAndDeletedFalse(String ruleCode);

    /**
     * 查询所有启用的规则，按优先级排序
     */
    List<AssignmentRule> findByDeletedFalseAndStatusOrderByRuleOrderAsc(RuleStatus status);

    /**
     * 匹配适用条件的规则
     */
    @Query("SELECT r FROM AssignmentRule r WHERE r.deleted = false AND r.status = 'ACTIVE' " +
            "AND (r.diseaseCategory IS NULL OR r.diseaseCategory = :diseaseCategory) " +
            "AND (r.hospitalArea IS NULL OR r.hospitalArea = :hospitalArea) " +
            "AND (r.department IS NULL OR r.department = :department) " +
            "ORDER BY r.ruleOrder ASC")
    List<AssignmentRule> findMatchingRules(
            @Param("diseaseCategory") String diseaseCategory,
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department);

    /**
     * 查询指定策略的规则
     */
    List<AssignmentRule> findByDeletedFalseAndAssignStrategyOrderByRuleOrderAsc(
            AssignmentRule.AssignStrategy strategy);
}
