package com.publichealth.public_health_api.module.cdcupload.repository;

import com.publichealth.public_health_api.module.cdcupload.entity.CdcUpload;
import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * CDC上报记录数据访问层
 */
@Repository
public interface CdcUploadRepository extends JpaRepository<CdcUpload, String> {

    // ============================================
    // 基础查询方法
    // ============================================

    /**
     * 根据报告卡ID查询上报记录（未删除）
     */
    Optional<CdcUpload> findByReportCardIdAndDeletedFalse(String reportCardId);

    /**
     * 根据上报状态查询（分页）
     */
    Page<CdcUpload> findByUploadStatusAndDeletedFalse(UploadStatus uploadStatus, Pageable pageable);

    /**
     * 统计指定上报状态的数量
     */
    long countByUploadStatusAndDeletedFalse(UploadStatus uploadStatus);

    /**
     * 统计未删除记录总数
     */
    long countByDeletedFalse();

    // ============================================
    // 自定义 JPQL 查询
    // ============================================

    /**
     * 联合查询：获取已审核通过的报告卡及其上报状态
     * 支持关键词搜索、状态筛选、科室筛选、日期范围
     */
    @Query("SELECT cu FROM CdcUpload cu " +
           "INNER JOIN ReportCard rc ON cu.reportCardId = rc.id " +
           "WHERE rc.deleted = false AND cu.deleted = false " +
           "AND rc.auditStatus = 'APPROVED' " +
           "AND (:keyword IS NULL OR LOWER(rc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(rc.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:uploadStatus IS NULL OR cu.uploadStatus = :uploadStatus) " +
           "AND (:department IS NULL OR rc.department = :department) " +
           "AND (:fillDateStart IS NULL OR rc.fillDate >= :fillDateStart) " +
           "AND (:fillDateEnd IS NULL OR rc.fillDate <= :fillDateEnd)")
    Page<CdcUpload> findApprovedWithUploadStatus(
            @Param("keyword") String keyword,
            @Param("uploadStatus") UploadStatus uploadStatus,
            @Param("department") String department,
            @Param("fillDateStart") LocalDate fillDateStart,
            @Param("fillDateEnd") LocalDate fillDateEnd,
            Pageable pageable
    );

    /**
     * 查询已审核通过但尚未创建上报记录的报告卡ID列表
     */
    @Query("SELECT rc.id FROM ReportCard rc " +
           "WHERE rc.deleted = false " +
           "AND rc.auditStatus = 'APPROVED' " +
           "AND rc.id NOT IN (SELECT cu.reportCardId FROM CdcUpload cu WHERE cu.deleted = false)")
    List<String> findApprovedWithoutUploadRecord();

    /**
     * 查询所有已审核通过的报告卡ID列表（含上报记录）
     */
    @Query("SELECT rc.id FROM ReportCard rc " +
           "WHERE rc.deleted = false AND rc.auditStatus = 'APPROVED'")
    List<String> findAllApprovedReportCardIds();

    /**
     * 原子操作：为已审核通过但无上报记录的报告卡批量创建记录
     * 使用 INSERT IGNORE 避免并发重复插入导致唯一约束冲突
     */
    @Modifying
    @Query(value = "INSERT IGNORE INTO cdc_upload (id, report_card_id, upload_status, retry_count, deleted, create_time) " +
           "SELECT UUID(), rc.id, 'NOT_UPLOADED', 0, 0, NOW() " +
           "FROM report_card rc " +
           "WHERE rc.deleted = 0 AND rc.audit_status = 'APPROVED' " +
           "AND rc.id NOT IN (SELECT cu.report_card_id FROM cdc_upload cu WHERE cu.deleted = 0)",
           nativeQuery = true)
    int insertMissingUploadRecords();
}
