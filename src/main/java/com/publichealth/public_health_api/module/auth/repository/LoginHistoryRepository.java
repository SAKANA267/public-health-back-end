package com.publichealth.public_health_api.module.auth.repository;

import com.publichealth.public_health_api.module.auth.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录历史数据访问层
 * 继承 JpaRepository 获得基础 CRUD 功能
 */
@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, String> {

    // ============================================
    // 基础查询方法 (Spring Data JPA 自动实现)
    // ============================================

    /**
     * 根据用户ID查询登录历史
     *
     * @param userId 用户ID
     * @return 登录历史列表，按登录时间倒序
     */
    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(String userId);

    /**
     * 根据用户ID和状态查询登录历史
     *
     * @param userId 用户ID
     * @param status 登录状态
     * @return 登录历史列表
     */
    List<LoginHistory> findByUserIdAndStatusOrderByLoginTimeDesc(String userId, LoginHistory.LoginStatus status);

    /**
     * 分页查询用户登录历史
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 登录历史分页结果
     */
    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(String userId, Pageable pageable);

    /**
     * 根据用户ID和状态分页查询
     *
     * @param userId   用户ID
     * @param status   登录状态
     * @param pageable 分页参数
     * @return 登录历史分页结果
     */
    Page<LoginHistory> findByUserIdAndStatusOrderByLoginTimeDesc(String userId, LoginHistory.LoginStatus status, Pageable pageable);

    // ============================================
    // 统计查询
    // ============================================

    /**
     * 统计指定用户的登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    long countByUserId(String userId);

    /**
     * 统计指定用户在指定状态的登录次数
     *
     * @param userId 用户ID
     * @param status 登录状态
     * @return 登录次数
     */
    long countByUserIdAndStatus(String userId, LoginHistory.LoginStatus status);

    /**
     * 统计指定时间范围内的登录失败次数
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 失败次数
     */
    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.userId = :userId " +
           "AND lh.status = 'FAILURE' AND lh.loginTime BETWEEN :startTime AND :endTime")
    long countFailedLoginsInPeriod(@Param("userId") String userId,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    // ============================================
    // 时间范围查询
    // ============================================

    /**
     * 查询指定时间范围内的登录历史
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 登录历史列表
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId " +
           "AND lh.loginTime BETWEEN :startTime AND :endTime ORDER BY lh.loginTime DESC")
    List<LoginHistory> findByUserIdAndTimeRange(@Param("userId") String userId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询指定时间范围内的登录历史
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页参数
     * @return 登录历史分页结果
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId " +
           "AND lh.loginTime BETWEEN :startTime AND :endTime")
    Page<LoginHistory> findByUserIdAndTimeRange(@Param("userId") String userId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime,
                                                  Pageable pageable);

    // ============================================
    // 最近记录查询
    // ============================================

    /**
     * 查询用户最近的N条登录记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 登录历史列表
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId " +
           "ORDER BY lh.loginTime DESC")
    List<LoginHistory> findRecentByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 查询用户最后一次成功登录记录
     *
     * @param userId 用户ID
     * @return 最后一次成功登录记录，如果不存在返回null
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId " +
           "AND lh.status = 'SUCCESS' ORDER BY lh.loginTime DESC")
    List<LoginHistory> findLastSuccessLogin(@Param("userId") String userId, Pageable pageable);
}
