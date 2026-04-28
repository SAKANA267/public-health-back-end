package com.publichealth.public_health_api.module.reportcard.repository;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 传染病报告卡审核记录数据访问层
 */
@Repository
public interface ReportCardAuditRepository extends JpaRepository<ReportCardAudit, String> {

    Optional<ReportCardAudit> findByReportCardId(String reportCardId);

    List<ReportCardAudit> findByAuditStatusAndAssignStatus(
            ReportCardAudit.AuditStatus auditStatus,
            ReportCardAudit.AssignStatus assignStatus
    );

    List<ReportCardAudit> findByAssigneeId(String assigneeId);

    List<ReportCardAudit> findByAuditorId(String auditorId);
}
