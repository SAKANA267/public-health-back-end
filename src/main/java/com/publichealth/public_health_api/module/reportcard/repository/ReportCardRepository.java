package com.publichealth.public_health_api.module.reportcard.repository;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 传染病报告卡数据访问层
 * 迁移说明：主表查询，部分字段已移至关联表
 */
@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, String> {

    Optional<ReportCard> findByInpatientNo(String inpatientNo);

    Optional<ReportCard> findByInpatientNoAndDeletedFalse(String inpatientNo);

    Optional<ReportCard> findByOutpatientNoAndDeletedFalse(String outpatientNo);

    List<ReportCard> findByAuditStatusAndDeletedFalse(ReportCard.AuditStatus auditStatus);

    Page<ReportCard> findByAuditStatusAndDeletedFalse(ReportCard.AuditStatus auditStatus, Pageable pageable);

    List<ReportCard> findByAuditStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.AuditStatus auditStatus);

    Page<ReportCard> findByAuditStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.AuditStatus auditStatus, Pageable pageable);

    List<ReportCard> findByHospitalAreaAndDeletedFalse(String hospitalArea);

    Page<ReportCard> findByHospitalAreaAndDeletedFalse(String hospitalArea, Pageable pageable);

    List<ReportCard> findByDepartmentAndDeletedFalse(String department);

    Page<ReportCard> findByDepartmentAndDeletedFalse(String department, Pageable pageable);

    Page<ReportCard> findByHospitalAreaAndDepartmentAndDeletedFalse(
            String hospitalArea, String department, Pageable pageable);

    List<ReportCard> findByFillDateAndDeletedFalse(LocalDate fillDate);

    List<ReportCard> findByFillDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    Page<ReportCard> findByFillDateBetweenAndDeletedFalse(LocalDate start, LocalDate end, Pageable pageable);

    List<ReportCard> findByDeletedFalse();

    List<ReportCard> findByDeletedTrue();

    Page<ReportCard> findByDeletedFalse(Pageable pageable);

    @Query("SELECT r FROM ReportCard r WHERE " +
            "(:includeDeleted = true OR r.deleted = false) AND " +
            "(:keyword IS NULL OR LOWER(r.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR r.auditStatus = :status) AND " +
            "(:hospitalArea IS NULL OR r.hospitalArea = :hospitalArea) AND " +
            "(:department IS NULL OR r.department = :department) AND " +
            "(:auditorId IS NULL OR EXISTS (" +
            "   SELECT 1 FROM ReportCardAudit a WHERE a.reportCardId = r.id AND a.auditorId = :auditorId" +
            ")) AND " +
            "(:startTime IS NULL OR r.createTime >= :startTime) AND " +
            "(:endTime IS NULL OR r.createTime <= :endTime)")
    Page<ReportCard> findByConditions(
            @Param("keyword") String keyword,
            @Param("status") ReportCard.AuditStatus status,
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("auditorId") String auditorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("includeDeleted") Boolean includeDeleted,
            Pageable pageable
    );

    @Query("SELECT DISTINCT r FROM ReportCard r " +
            "INNER JOIN com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment a " +
            "ON r.id = a.reportCardId " +
            "WHERE a.auditGroupId IN :groupIds " +
            "AND r.deleted = false " +
            "AND a.deleted = false " +
            "AND a.status IN ('PENDING', 'IN_PROGRESS')")
    Page<ReportCard> findByAuditGroupIds(
            @Param("groupIds") List<String> groupIds,
            Pageable pageable
    );

    @Query("SELECT DISTINCT r FROM ReportCard r " +
            "INNER JOIN com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment a " +
            "ON r.id = a.reportCardId " +
            "WHERE a.auditGroupId IN :groupIds " +
            "AND r.deleted = false " +
            "AND a.deleted = false " +
            "AND a.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND (:keyword IS NULL OR LOWER(r.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR r.auditStatus = :status) " +
            "AND (:hospitalArea IS NULL OR r.hospitalArea = :hospitalArea) " +
            "AND (:department IS NULL OR r.department = :department) " +
            "AND (:auditorId IS NULL OR EXISTS (" +
            "   SELECT 1 FROM ReportCardAudit aud WHERE aud.reportCardId = r.id AND aud.auditorId = :auditorId" +
            ")) " +
            "AND (:startTime IS NULL OR r.createTime >= :startTime) AND " +
            "(:endTime IS NULL OR r.createTime <= :endTime)")
    Page<ReportCard> findByAuditGroupIdsAndConditions(
            @Param("groupIds") List<String> groupIds,
            @Param("keyword") String keyword,
            @Param("status") ReportCard.AuditStatus status,
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("auditorId") String auditorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    @Query("SELECT DISTINCT r FROM ReportCard r " +
            "INNER JOIN com.publichealth.public_health_api.module.assignment.entity.ReportCardAssignment a " +
            "ON r.id = a.reportCardId " +
            "WHERE a.auditGroupId IN :groupIds " +
            "AND r.deleted = false " +
            "AND a.deleted = false " +
            "AND a.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND (LOWER(r.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ReportCard> searchByAuditGroupIds(
            @Param("groupIds") List<String> groupIds,
            @Param("keyword") String keyword
    );

    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
            "(LOWER(r.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ReportCard> searchRecords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
            "(LOWER(r.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ReportCard> searchRecordsList(@Param("keyword") String keyword);

    @Query("SELECT r.auditStatus, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.auditStatus")
    List<Object[]> countByStatus();

    long countByAuditStatusAndDeletedFalse(ReportCard.AuditStatus auditStatus);

    @Query("SELECT COUNT(r) FROM ReportCard r WHERE r.deleted = false AND DATE(r.createTime) = CURRENT_DATE")
    long countTodayNew();

    long countByDeletedFalse();

    @Query("SELECT r.diseaseName, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.diseaseName ORDER BY COUNT(r) DESC")
    List<Object[]> countByDiagnosisGroup();

    @Query("SELECT r.hospitalArea, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.hospitalArea ORDER BY COUNT(r) DESC")
    List<Object[]> countByHospitalAreaGroup();

    @Query(value = "SELECT DAYNAME(r.create_time), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND r.create_time >= :startDate GROUP BY DAYNAME(r.create_time) ORDER BY r.create_time", nativeQuery = true)
    List<Object[]> countByLast7Days(@Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT CONCAT('第', WEEK(r.create_time) - WEEK(DATE_FORMAT(r.create_time, '%Y-%m-01')) + 1, '周'), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND YEAR(r.create_time) = YEAR(CURRENT_DATE) AND MONTH(r.create_time) = MONTH(CURRENT_DATE) GROUP BY WEEK(r.create_time) ORDER BY WEEK(r.create_time)", nativeQuery = true)
    List<Object[]> countByWeeksInMonth();

    @Query(value = "SELECT CONCAT(MONTH(r.create_time), '月'), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND YEAR(r.create_time) = YEAR(CURRENT_DATE) GROUP BY MONTH(r.create_time) ORDER BY MONTH(r.create_time)", nativeQuery = true)
    List<Object[]> countByMonthsInYear();

    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false ORDER BY r.updateTime DESC")
    List<ReportCard> findRecentUpdatedRecords(Pageable pageable);

    @Query("UPDATE ReportCard r SET r.deleted = true WHERE r.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int logicalDeleteById(@Param("id") String id);

    @Query("UPDATE ReportCard r SET r.auditStatus = :status WHERE r.id IN :ids")
    @org.springframework.data.jpa.repository.Modifying
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") ReportCard.AuditStatus status);

    boolean existsByInpatientNo(String inpatientNo);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReportCard r " +
            "WHERE r.inpatientNo = :inpatientNo AND r.id != :id AND r.deleted = false")
    boolean existsByInpatientNoAndIdNot(@Param("inpatientNo") String inpatientNo, @Param("id") String id);

    boolean existsByOutpatientNo(String outpatientNo);
}
