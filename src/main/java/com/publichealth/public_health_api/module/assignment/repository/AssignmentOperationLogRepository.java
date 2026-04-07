package com.publichealth.public_health_api.module.assignment.repository;

import com.publichealth.public_health_api.module.assignment.entity.AssignmentOperationLog;
import com.publichealth.public_health_api.module.assignment.entity.AssignmentOperationLog.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务操作日志Repository
 */
@Repository
public interface AssignmentOperationLogRepository extends JpaRepository<AssignmentOperationLog, String> {

    /**
     * 根据任务ID查询操作日志
     */
    List<AssignmentOperationLog> findByAssignmentIdOrderByCreateTimeDesc(String assignmentId);

    /**
     * 根据报卡ID查询操作日志
     */
    List<AssignmentOperationLog> findByReportCardIdOrderByCreateTimeDesc(String reportCardId);

    /**
     * 根据操作类型查询日志
     */
    List<AssignmentOperationLog> findByOperationTypeOrderByCreateTimeDesc(OperationType operationType);

    /**
     * 根据操作人查询日志
     */
    List<AssignmentOperationLog> findByOperatorIdOrderByCreateTimeDesc(String operatorId);

    /**
     * 查询指定时间范围内的日志
     */
    List<AssignmentOperationLog> findByCreateTimeBetweenOrderByCreateTimeDesc(
            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据任务ID和操作类型查询
     */
    List<AssignmentOperationLog> findByAssignmentIdAndOperationTypeOrderByCreateTimeDesc(
            String assignmentId, OperationType operationType);
}
