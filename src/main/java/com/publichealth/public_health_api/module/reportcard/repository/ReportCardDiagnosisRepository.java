package com.publichealth.public_health_api.module.reportcard.repository;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCardDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 传染病报告卡诊断信息数据访问层
 */
@Repository
public interface ReportCardDiagnosisRepository extends JpaRepository<ReportCardDiagnosis, String> {

    Optional<ReportCardDiagnosis> findByReportCardId(String reportCardId);
}
