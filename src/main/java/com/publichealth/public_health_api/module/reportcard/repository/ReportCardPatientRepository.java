package com.publichealth.public_health_api.module.reportcard.repository;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 传染病报告卡患者信息数据访问层
 */
@Repository
public interface ReportCardPatientRepository extends JpaRepository<ReportCardPatient, String> {

    Optional<ReportCardPatient> findByReportCardId(String reportCardId);

    boolean existsByIdCard(String idCard);
}
