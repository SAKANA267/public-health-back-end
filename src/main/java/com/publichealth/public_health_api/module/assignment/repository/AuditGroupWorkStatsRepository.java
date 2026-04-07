package com.publichealth.public_health_api.module.assignment.repository;

import com.publichealth.public_health_api.module.assignment.entity.AuditGroupWorkStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 审核组工作统计Repository
 */
@Repository
public interface AuditGroupWorkStatsRepository extends JpaRepository<AuditGroupWorkStats, String> {

    /**
     * 根据审核组ID查询统计
     */
    Optional<AuditGroupWorkStats> findByAuditGroupIdAndDeletedFalse(String auditGroupId);

    /**
     * 查询所有活跃审核组的统计
     */
    @Query("SELECT s FROM AuditGroupWorkStats s WHERE s.deleted = false " +
            "ORDER BY (s.pendingCount + s.inProgressCount) ASC")
    List<AuditGroupWorkStats> findAllActiveOrderByCurrentTaskCount();

    /**
     * 查询任务数最少的审核组
     */
    @Query("SELECT s FROM AuditGroupWorkStats s WHERE s.deleted = false " +
            "ORDER BY (s.pendingCount + s.inProgressCount) ASC LIMIT 1")
    Optional<AuditGroupWorkStats> findLeastLoadedGroup();

    /**
     * 查询所有活跃审核组
     */
    List<AuditGroupWorkStats> findByDeletedFalse();
}
