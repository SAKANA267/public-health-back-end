package com.publichealth.public_health_api.module.reportcard.repository;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
     * 根据状态查询未删除的列表
     */
    List<ReportCard> findByStatusAndDeletedFalse(ReportCard.ReportStatus status);

    /**
     * 根据状态查询未删除的列表 (分页)
     */
    Page<ReportCard> findByStatusAndDeletedFalse(ReportCard.ReportStatus status, Pageable pageable);

    /**
     * 查询所有待审核的报告卡
     */
    List<ReportCard> findByStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.ReportStatus status);

    /**
     * 查询所有待审核的报告卡 (分页)
     */
    Page<ReportCard> findByStatusAndDeletedFalseOrderByFillDateDesc(ReportCard.ReportStatus status, Pageable pageable);

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
           "r.status = :status AND r.fillDate BETWEEN :start AND :end")
    Page<ReportCard> findByStatusAndDateRange(
            @Param("status") ReportCard.ReportStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);

    /**
     * 复合查询: 院区 + 科室 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.hospitalArea = :hospitalArea AND r.department = :department AND r.status = :status")
    Page<ReportCard> findByHospitalAreaAndDepartmentAndStatus(
            @Param("hospitalArea") String hospitalArea,
            @Param("department") String department,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 复合查询: 院区 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.hospitalArea = :hospitalArea AND r.status = :status")
    Page<ReportCard> findByHospitalAreaAndStatus(
            @Param("hospitalArea") String hospitalArea,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 复合查询: 科室 + 状态
     */
    @Query("SELECT r FROM ReportCard r WHERE r.deleted = false AND " +
           "r.department = :department AND r.status = :status")
    Page<ReportCard> findByDepartmentAndStatus(
            @Param("department") String department,
            @Param("status") ReportCard.ReportStatus status,
            Pageable pageable);

    /**
     * 统计各状态的数量
     */
    @Query("SELECT r.status, COUNT(r) FROM ReportCard r WHERE r.deleted = false GROUP BY r.status")
    List<Object[]> countByStatus();

    /**
     * 统计指定状态的数量
     */
    long countByStatusAndDeletedFalse(ReportCard.ReportStatus status);

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
    @Query("UPDATE ReportCard r SET r.status = :status WHERE r.id IN :ids")
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
