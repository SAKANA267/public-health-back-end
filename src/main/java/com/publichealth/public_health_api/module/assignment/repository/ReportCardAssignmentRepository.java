package com.publichealth.public_health_api.module.assignment.repository;

import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment;
import com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 报卡分配记录Repository
 */
@Repository
public interface ReportCardAssignmentRepository extends JpaRepository<ReportCardAssignment, String> {

    /**
     * 根据报卡ID查询未完成的分配记录
     */
    Optional<ReportCardAssignment> findByReportCardIdAndDeletedFalseAndStatusNot(
            String reportCardId, AssignmentStatus status);

    /**
     * 根据审核组ID查询待处理任务
     */
    List<ReportCardAssignment> findByAuditGroupIdAndDeletedFalseAndStatusOrderByAssignTimeDesc(
            String auditGroupId, AssignmentStatus status);

    /**
     * 查询即将超时的任务
     */
    List<ReportCardAssignment> findByDeletedFalseAndStatusAndDeadlineBeforeOrderByDeadlineAsc(
            AssignmentStatus status, LocalDateTime deadline);

    /**
     * 查询已超时的任务
     */
    @Query("SELECT a FROM ReportCardAssignment a WHERE a.deleted = false " +
            "AND a.status IN :statuses AND a.deadline < :now " +
            "ORDER BY a.deadline ASC")
    List<ReportCardAssignment> findOverdueAssignments(
            @Param("statuses") List<AssignmentStatus> statuses,
            @Param("now") LocalDateTime now);

    /**
     * 统计审核组当前任务数
     */
    @Query("SELECT COUNT(a) FROM ReportCardAssignment a WHERE a.auditGroupId = :groupId " +
            "AND a.deleted = false AND a.status IN :statuses")
    Long countByGroupIdAndStatuses(
            @Param("groupId") String groupId,
            @Param("statuses") List<AssignmentStatus> statuses);

    /**
     * 查询指定时间范围内的分配记录
     */
    List<ReportCardAssignment> findByDeletedFalseAndAssignTimeBetweenOrderByAssignTimeDesc(
            LocalDateTime startTime, LocalDateTime endTime);
}
