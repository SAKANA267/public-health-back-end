package com.publichealth.public_health_api.module.auth.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryQueryRequest;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录历史服务接口
 * 定义登录历史相关的业务操作
 */
public interface LoginHistoryService {

    // ============================================
    // 记录登录历史
    // ============================================

    /**
     * 记录登录成功
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param ipAddress IP地址
     * @param userAgent 浏览器信息
     */
    void recordLoginSuccess(String userId, String username, String ipAddress, String userAgent);

    /**
     * 记录登录成功（带登录地点）
     *
     * @param userId         用户ID
     * @param username       用户名
     * @param ipAddress      IP地址
     * @param userAgent      浏览器信息
     * @param loginLocation  登录地点
     */
    void recordLoginSuccess(String userId, String username, String ipAddress, String userAgent, String loginLocation);

    /**
     * 记录登录失败
     *
     * @param username    用户名
     * @param ipAddress   IP地址
     * @param userAgent   浏览器信息
     * @param failReason  失败原因
     */
    void recordLoginFailure(String username, String ipAddress, String userAgent, String failReason);

    /**
     * 记录登录失败（带用户ID）
     *
     * @param userId      用户ID（如果用户存在）
     * @param username    用户名
     * @param ipAddress   IP地址
     * @param userAgent   浏览器信息
     * @param failReason  失败原因
     */
    void recordLoginFailure(String userId, String username, String ipAddress, String userAgent, String failReason);

    // ============================================
    // 查询登录历史
    // ============================================

    /**
     * 根据用户ID查询登录历史
     *
     * @param userId 用户ID
     * @return 登录历史响应列表
     */
    List<LoginHistoryResponse> getLoginHistoryByUserId(String userId);

    /**
     * 根据用户ID分页查询登录历史
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<LoginHistoryResponse> getLoginHistoryWithPaging(LoginHistoryQueryRequest request);

    /**
     * 查询用户最近的登录记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 登录历史响应列表
     */
    List<LoginHistoryResponse> getRecentLoginsByUserId(String userId, int limit);

    // ============================================
    // 统计查询
    // ============================================

    /**
     * 统计用户登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    long countLoginsByUserId(String userId);

    /**
     * 统计用户登录失败次数（指定时间范围内）
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 失败次数
     */
    long countFailedLoginsInPeriod(String userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户最后一次成功登录记录
     *
     * @param userId 用户ID
     * @return 最后一次成功登录记录，如果不存在返回null
     */
    LoginHistoryResponse getLastSuccessLogin(String userId);

    // ============================================
    // 删除操作
    // ============================================

    /**
     * 删除指定时间范围之前的登录历史
     *
     * @param beforeTime 指定时间
     * @return 删除的记录数
     */
    int deleteLoginHistoryBefore(LocalDateTime beforeTime);

    /**
     * 删除指定用户的登录历史
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteLoginHistoryByUserId(String userId);
}
