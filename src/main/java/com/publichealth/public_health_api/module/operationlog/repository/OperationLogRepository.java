package com.publichealth.public_health_api.module.operationlog.repository;

import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;
import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问层
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, String> {

    // ============================================
    // 用户相关查询
    // ============================================

    /**
     * 根据用户ID查询日志列表
     */
    List<OperationLog> findByUserId(String userId);

    /**
     * 根据用户ID查询日志列表（分页）
     */
    Page<OperationLog> findByUserId(String userId, Pageable pageable);

    /**
     * 根据用户名查询日志列表（分页）
     */
    Page<OperationLog> findByUsername(String username, Pageable pageable);

    /**
     * 查询指定用户的失败操作日志
     */
    List<OperationLog> findByUserIdAndStatus(String userId, OperationStatus status);

    // ============================================
    // 模块相关查询
    // ============================================

    /**
     * 根据模块名称查询日志列表（分页）
     */
    Page<OperationLog> findByModule(String module, Pageable pageable);

    /**
     * 统计指定模块的日志数量
     */
    long countByModule(String module);

    // ============================================
    // 操作类型相关查询
    // ============================================

    /**
     * 根据操作类型查询日志列表（分页）
     */
    Page<OperationLog> findByOperationType(OperationType operationType, Pageable pageable);

    /**
     * 统计指定操作类型的日志数量
     */
    long countByOperationType(OperationType operationType);

    // ============================================
    // 状态相关查询
    // ============================================

    /**
     * 根据操作状态查询日志列表（分页）
     */
    Page<OperationLog> findByStatus(OperationStatus status, Pageable pageable);

    /**
     * 统计失败的操作数量
     */
    long countByStatus(OperationStatus status);

    // ============================================
    // 时间范围查询
    // ============================================

    /**
     * 查询指定时间之后的日志
     */
    List<OperationLog> findByCreateTimeAfter(LocalDateTime dateTime);

    /**
     * 查询指定时间之前的日志（用于清理）
     */
    List<OperationLog> findByCreateTimeBefore(LocalDateTime dateTime);

    /**
     * 查询时间范围内的日志
     */
    List<OperationLog> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 查询时间范围内的日志（分页）
     */
    Page<OperationLog> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // ============================================
    // 复合查询
    // ============================================

    /**
     * 根据用户ID、模块、操作类型、状态、时间范围查询日志
     */
    @Query("SELECT l FROM OperationLog l WHERE " +
           "(:userId IS NULL OR l.userId = :userId) AND " +
           "(:module IS NULL OR l.module = :module) AND " +
           "(:operationType IS NULL OR l.operationType = :operationType) AND " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:startTime IS NULL OR l.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR l.createTime <= :endTime) AND " +
           "(:keyword IS NULL OR l.operation LIKE %:keyword% OR l.method LIKE %:keyword%) AND " +
           "(:ipAddress IS NULL OR l.ipAddress = :ipAddress)")
    Page<OperationLog> findByConditions(
            @Param("userId") String userId,
            @Param("module") String module,
            @Param("operationType") OperationType operationType,
            @Param("status") OperationStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword,
            @Param("ipAddress") String ipAddress,
            Pageable pageable
    );

    // ============================================
    // 统计查询
    // ============================================

    /**
     * 统计各模块的操作数量
     */
    @Query("SELECT l.module, COUNT(l) FROM OperationLog l GROUP BY l.module")
    List<Object[]> countByModuleGroupBy();

    /**
     * 统计各操作类型的数量
     */
    @Query("SELECT l.operationType, COUNT(l) FROM OperationLog l GROUP BY l.operationType")
    List<Object[]> countByOperationTypeGroupBy();

    /**
     * 统计平均耗时
     */
    @Query("SELECT AVG(l.costTime) FROM OperationLog l WHERE l.costTime IS NOT NULL")
    Double getAvgCostTime();

    /**
     * 统计最大耗时
     */
    @Query("SELECT MAX(l.costTime) FROM OperationLog l WHERE l.costTime IS NOT NULL")
    Long getMaxCostTime();

    /**
     * 统计指定时间范围内的操作数量
     */
    @Query("SELECT COUNT(l) FROM OperationLog l WHERE " +
           "l.createTime >= :startTime AND l.createTime <= :endTime")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定用户在时间范围内的成功操作数量
     */
    @Query("SELECT COUNT(l) FROM OperationLog l WHERE " +
           "l.userId = :userId AND l.status = :status AND " +
           "l.createTime >= :startTime AND l.createTime <= :endTime")
    long countByUserAndStatusAndTimeRange(@Param("userId") String userId,
                                           @Param("status") OperationStatus status,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    // ============================================
    // 删除操作
    // ============================================

    /**
     * 删除指定时间之前的日志
     */
    @Query("DELETE FROM OperationLog l WHERE l.createTime < :dateTime")
    int deleteByCreateTimeBefore(@Param("dateTime") LocalDateTime dateTime);
}
