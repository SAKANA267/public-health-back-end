package com.publichealth.public_health_api.module.auth.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryQueryRequest;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryResponse;
import com.publichealth.public_health_api.module.auth.service.LoginHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录历史控制器
 * 处理登录历史查询相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/login-history")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询用户登录历史
     * GET /api/auth/login-history
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResult<LoginHistoryResponse>> getLoginHistory(
            @Valid LoginHistoryQueryRequest request) {
        log.info("查询登录历史: userId={}, page={}, size={}",
                request.getUserId(), request.getPage(), request.getSize());

        PageResult<LoginHistoryResponse> result = loginHistoryService.getLoginHistoryWithPaging(request);
        return ApiResponse.success("查询成功", result);
    }

    /**
     * 获取用户所有登录历史
     * GET /api/auth/login-history/user/{userId}
     *
     * @param userId 用户ID
     * @return 登录历史列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<LoginHistoryResponse>> getLoginHistoryByUserId(@PathVariable String userId) {
        log.info("查询用户登录历史: userId={}", userId);

        List<LoginHistoryResponse> result = loginHistoryService.getLoginHistoryByUserId(userId);
        return ApiResponse.success("查询成功", result);
    }

    /**
     * 获取用户最近的登录记录
     * GET /api/auth/login-history/recent?userId=xxx&limit=10
     *
     * @param userId 用户ID
     * @param limit  限制数量，默认10
     * @return 登录历史列表
     */
    @GetMapping("/recent")
    public ApiResponse<List<LoginHistoryResponse>> getRecentLogins(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("查询最近登录记录: userId={}, limit={}", userId, limit);

        List<LoginHistoryResponse> result = loginHistoryService.getRecentLoginsByUserId(userId, limit);
        return ApiResponse.success("查询成功", result);
    }

    /**
     * 获取用户最后一次成功登录记录
     * GET /api/auth/login-history/last-success/{userId}
     *
     * @param userId 用户ID
     * @return 最后一次成功登录记录
     */
    @GetMapping("/last-success/{userId}")
    public ApiResponse<LoginHistoryResponse> getLastSuccessLogin(@PathVariable String userId) {
        log.info("查询最后一次成功登录: userId={}", userId);

        LoginHistoryResponse result = loginHistoryService.getLastSuccessLogin(userId);
        return ApiResponse.success("查询成功", result);
    }

    // ============================================
    // 统计操作
    // ============================================

    /**
     * 统计用户登录次数
     * GET /api/auth/login-history/count/{userId}
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    @GetMapping("/count/{userId}")
    public ApiResponse<Long> countLogins(@PathVariable String userId) {
        log.info("统计用户登录次数: userId={}", userId);

        long count = loginHistoryService.countLoginsByUserId(userId);
        return ApiResponse.success("统计成功", count);
    }

    // ============================================
    // 删除操作
    // ============================================

    /**
     * 删除用户的所有登录历史
     * DELETE /api/auth/login-history/user/{userId}
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @DeleteMapping("/user/{userId}")
    public ApiResponse<Integer> deleteLoginHistoryByUserId(@PathVariable String userId) {
        log.info("删除用户登录历史: userId={}", userId);

        int count = loginHistoryService.deleteLoginHistoryByUserId(userId);
        return ApiResponse.success("删除成功", count);
    }
}
