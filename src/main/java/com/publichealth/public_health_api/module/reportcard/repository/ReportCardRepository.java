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
 * 继承 JpaRepository 获得基础 CRUD 功能
 */
@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, String> {

    // ============================================
    // 基础查询方法 (Spring Data JPA 自动实现)
    // ============================================

    /**
     * 根据住院号查询
     */
    Optional<ReportCard> findByInpatientNo(String inpatientNo);

    /**
     * 根据住院号查询未删除的记录
     */
    Optional<ReportCard> findByInpatientNoAndDeletedFalse(String inpatientNo);

    /**
     * 根据门诊号查询未删除的记录
     */
    Optional<ReportCard> findByOutpatientNoAndDeletedFalse(String outpatientNo);

    // ============================================
    // 状态查询
    // ============================================

    /**
     * 根据审核状态查询未删除的列表
     */
    List<ReportCard> findByAuditStatusAndDeletedFalse(ReportCard.ReportStatus auditStatus);

    /**
     * 根据审核状态查询未删除的列表 (分页)
     */
    Page<ReportCard> findByAuditStatusAndDeletedFalse(ReportCard.ReportStatus auditStatus, Pageable pageable);

    /**
     * 查询所有待审核的报告卡
     */
    List<ReportCard> findByAuditStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.ReportStatus auditStatus);

    /**
     * 查询所有待审核的报告卡 (分页)
     */
    Page<ReportCard> findByAuditStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.ReportStatus auditStatus, Pageable pageable);

    // ============================================
    // 分配状态查询
    // ============================================

    /**
     * 根据分配状态查询未删除的列表
     */
    List<ReportCard> findByAssignStatusAndDeletedFalse(ReportCard.AssignStatus assignStatus);

    /**
     * 根据分配状态查询未删除的列表 (分页)
     */
    Page<ReportCard> findByAssignStatusAndDeletedFalse(ReportCard.AssignStatus assignStatus, Pageable pageable);

    /**
     * 查询所有未分配的报告卡
     */
    List<ReportCard> findByAssignStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.AssignStatus assignStatus);

    /**
     * 查询所有未分配的报告卡 (分页)
     */
    Page<ReportCard> findByAssignStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.AssignStatus assignStatus, Pageable pageable);

    // ============================================
    // 院区/科室查询
    // ============================================

    /**
     * 根据院区查询未删除的列表
     */
    List<ReportCard> findByHospitalAreaAndDeletedFalse(String hospitalArea);

    /**
     * 根据院区查询未删除的列表 (分页)
     */
    Page<ReportCard> findByHospitalAreaAndDeletedFalse(String hospitalArea, Pageable pageable);

    /**
     * 根据科室查询未删除的列表
     */
    List<ReportCard> findByDepartmentAndDeletedFalse(String department);

    /**
     * 根据科室查询未删除的列表 (分页)
     */
    Page<ReportCard> findByDepartmentAndDeletedFalse(String department, Pageable pageable);

    /**
     * 根据院区和科室查询未删除的列表 (分页)
     */
    Page<ReportCard> findByHospitalAreaAndDepartmentAndDeletedFalse(
            String hospitalArea, String department, Pageable pageable);

    // ============================================
    // 时间范围查询
    // ============================================

    /**
     * 查询指定填报日期的记录
     */
    List<ReportCard> findByFillDateAndDeletedFalse(LocalDate fillDate);

    /**
     * 查询指定填报日期范围内的记录
     */
    List<ReportCard> findByFillDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    /**
     * 查询指定填报日期范围内的记录 (分页)
     */
    Page<ReportCard> findByFillDateBetweenAndDeletedFalse(LocalDate start, LocalDate end, Pageable pageable);

    /**
     * 查询指定审核日期范围内的记录
     */
    List<ReportCard> findByAuditDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    // ============================================
    // 审核人查询
    // ============================================

    /**
     * 根据审核人ID查询未删除的列表
     */
    List<ReportCard> findByAuditorIdAndDeletedFalse(String auditorId);

    /**
     * 根据审核人ID查询未删除的列表 (分页)
     */
    Page<ReportCard> findByAuditorIdAndDeletedFalse(String auditorId, Pageable pageable);

    // ============================================
    // 逻辑删除相关查询
    // ============================================

    /**
     * 查询所有未删除的记录
     */
    List<ReportCard> findByDeletedFalse();

    /**
     * 查询所有已删除的记录
     */
    List<ReportCard> findByDeletedTrue();

    /**
     * 查询所有未删除的记录 (分页)
     */
    Page<ReportCard> findByDeletedFalse(Pageable pageable);

    // ============================================
    // 自定义 JPQL 查询
    // ============================================

    /**
     * 统一条件查询 (支持多条件组合)
     * 所有条件都是可选的，使用 IS NULL OR 模式实现动态查询
     */
    @Query("SELECT r FROM ReportCard r WHERE " +
           "(:includeDeleted = true OR r.deleted = false) AND " +
           "(:keyword IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR r.auditStatus = :status) AND " +
           "(:hospitalArea IS NULL OR r.hospitalArea = :hospitalArea) AND " +
           "(:department IS NULL OR r.department = :department) AND " +
           "(:auditorId IS NULL OR r.auditorId = :auditorId) AND " +
           "(:startTime IS NULL OR r.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR r.createTime <= :endTime)")
    Page<ReportCard> findByConditions(
            @Param("keyword") String keyword,
            @Param("status") ReportCard.ReportStatus status,
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("auditorId") String auditorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("includeDeleted") Boolean includeDeleted,
            Pageable pageable
    );

    /**
     * 根据审核组ID列表查询报告卡 (权限过滤)
     * 查询分配给指定审核组的报告卡
     */
    @Query("SELECT DISTINCT r FROM ReportCard r " +
           "INNER JOIN ReportCardAssignment a ON r.id = a.reportCardId " +
           "WHERE a.auditGroupId IN :groupIds " +
           "AND r.deleted = false " +
           "AND a.deleted = false " +
           "AND a.status IN ('PENDING', 'IN_PROGRESS')")
    Page<ReportCard> findByAuditGroupIds(
            @Param("groupIds") List<String> groupIds,
            Pageable pageable
    );

    /**
     * 根据审核组ID列表和条件查询报告卡 (权限过滤)
     * 支持多条件组合查询
     */
    @Query("SELECT DISTINCT r FROM ReportCard r " +
           "INNER JOIN ReportCardAssignment a ON r.id = a.reportCardId " +
           "WHERE a.auditGroupId IN :groupIds " +
           "AND r.deleted = false " +
           "AND a.deleted = false " +
           "AND a.status IN ('PENDING', 'IN_PROGRESS') " +
           "AND (:keyword IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR r.auditStatus = :status) " +
           "AND (:hospitalArea IS NULL OR r.hospitalArea = :hospitalArea) " +
           "AND (:department IS NULL OR r.department = :department) " +
           "AND (:auditorId IS NULL OR r.auditorId = :auditorId) " +
           "AND (:startTime IS NULL OR r.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR r.createTime <= :endTime)")
    Page<ReportCard> findByAuditGroupIdsAndConditions(
            @Param("groupIds") List<String> groupIds,
            @Param("keyword") String keyword,
            @Param("status") ReportCard.ReportStatus status,
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("auditorId") String auditorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    /**
     * 根据审核组ID列表搜索报告卡 (权限过滤)
     */
    @Query("SELECT DISTINCT r FROM ReportCard r " +
           "INNER JOIN ReportCardAssignment a ON r.id = a.reportCardId " +
           "WHERE a.auditGroupId IN :groupIds " +
           "AND r.deleted = false " +
           "AND a.deleted = false " +
           "AND a.status IN ('PENDING', 'IN_PROGRESS') " +
           "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ReportCard> searchByAuditGroupIds(
            @Param("groupIds") List<String> groupIds,
            @Param("keyword") String keyword
    );

    /**
     * 搜索报告卡 (模糊匹配患者姓名、诊断名称、住院号)
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ReportCard> searchRecords(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 搜索报告卡 (模糊匹配患者姓名、诊断名称、住院号) - 返回列表
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.diagnosisName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.inpatientNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ReportCard> searchRecordsList(@Param("keyword") String keyword);

    /**
     * 复合查询: 状态 + 填报日期范围
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.auditStatus = :status AND r.fillDate BETWEEN :start AND :end")
    Page<ReportCard> findByStatusAndDateRange(
            @Param("status") ReportCard.ReportStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);

    /**
     * 复合查询: 院区 + 科室 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.hospitalArea = :hospitalArea AND r.department = :department AND r.auditStatus = :status")
    Page<ReportCard> findByHospitalAreaAndDepartmentAndStatus(
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 复合查询: 院区 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.hospitalArea = :hospitalArea AND r.auditStatus = :status")
    Page<ReportCard> findByHospitalAreaAndStatus(
            @Param("hospitalArea") String hospitalArea,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 复合查询: 科室 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.department = :department AND r.auditStatus = :status")
    Page<ReportCard> findByDepartmentAndStatus(
            @Param("department") String department,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 统计各状态的数量
     */
    @Query("SELECT r.auditStatus, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.auditStatus")
    List<Object[]> countByStatus();

    /**
     * 统计指定状态的数量
     */
    long countByAuditStatusAndDeletedFalse(ReportCard.ReportStatus auditStatus);

    /**
     * 统计指定分配状态的数量
     */
    long countByAssignStatusAndDeletedFalse(ReportCard.AssignStatus assignStatus);

    /**
     * 统计今日新增数量
     */
    @Query("SELECT COUNT(r) FROM ReportCard r WHERE r.deleted = false AND DATE(r.createTime) = CURRENT_DATE")
    long countTodayNew();

    /**
     * 统计未删除的记录总数
     */
    long countByDeletedFalse();

    // ============================================
    // 统计相关查询 (Dashboard页面)
    // ============================================

    /**
     * 按疾病种类统计数量
     */
    @Query("SELECT r.diagnosisName, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.diagnosisName ORDER BY COUNT(r) DESC")
    List<Object[]> countByDiagnosisGroup();

    /**
     * 按院区统计数量
     */
    @Query("SELECT r.hospitalArea, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.hospitalArea ORDER BY COUNT(r) DESC")
    List<Object[]> countByHospitalAreaGroup();

    /**
     * 按最近7天统计数量（周趋势）- Native Query
     */
    @Query(value = "SELECT DAYNAME(r.create_time), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND r.create_time >= :startDate GROUP BY DAYNAME(r.create_time) ORDER BY r.create_time", nativeQuery = true)
    List<Object[]> countByLast7Days(@Param("startDate") LocalDateTime startDate);

    /**
     * 按当月每周统计数量（月趋势）- Native Query
     */
    @Query(value = "SELECT CONCAT('第', WEEK(r.create_time) - WEEK(DATE_FORMAT(r.create_time, '%Y-%m-01')) + 1, '周'), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND YEAR(r.create_time) = YEAR(CURRENT_DATE) AND MONTH(r.create_time) = MONTH(CURRENT_DATE) GROUP BY WEEK(r.create_time) ORDER BY WEEK(r.create_time)", nativeQuery = true)
    List<Object[]> countByWeeksInMonth();

    /**
     * 按当年每月统计数量（年趋势）- Native Query
     */
    @Query(value = "SELECT CONCAT(MONTH(r.create_time), '月'), COUNT(r.id) FROM report_card r WHERE r.deleted = false AND YEAR(r.create_time) = YEAR(CURRENT_DATE) GROUP BY MONTH(r.create_time) ORDER BY MONTH(r.create_time)", nativeQuery = true)
    List<Object[]> countByMonthsInYear();

    /**
     * 查询最近的报卡更新记录（用于最近活动）
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false ORDER BY r.updateTime DESC")
    List<ReportCard> findRecentUpdatedRecords(Pageable pageable);

    // ============================================
    // 修改/删除操作 (需要 @Modifying 注解)
    // ============================================

    /**
     * 逻辑删除记录
     */
    @Query("UPDATE ReportCard r SET r.deleted = true WHERE r.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int logicalDeleteById(@Param("id") String id);

    /**
     * 批量更新状态
     */
    @Query("UPDATE ReportCard r SET r.auditStatus = :status WHERE r.id IN :ids")
    @org.springframework.data.jpa.repository.Modifying
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") ReportCard.ReportStatus status);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查住院号是否存在
     */
    boolean existsByInpatientNo(String inpatientNo);

    /**
     * 检查住院号是否存在 (排除指定ID)
     * 用于更新时检查住院号是否与其他记录冲突
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReportCard r " +
           "WHERE r.inpatientNo = :inpatientNo AND r.id != :id AND r.deleted = false")
    boolean existsByInpatientNoAndIdNot(@Param("inpatientNo") String inpatientNo, @Param("id") String id);

    /**
     * 检查门诊号是否存在
     */
    boolean existsByOutpatientNo(String outpatientNo);
}
