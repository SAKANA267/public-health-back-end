package com.publichealth.public_health_api.module.sysuser.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.module.sysuser.dto.*;
import com.publichealth.public_health_api.module.sysuser.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户个人中心Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 获取用户统计数据
     * GET /api/user/profile/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<UserProfileStatisticsDTO> getStatistics(@RequestAttribute String userId) {
        log.info("获取用户统计数据: userId={}", userId);
        UserProfileStatisticsDTO statistics = userProfileService.getStatistics(userId);
        return ApiResponse.success(statistics);
    }

    /**
     * 获取用户贡献日历数据
     * GET /api/user/profile/contributions
     */
    @GetMapping("/contributions")
    public ApiResponse<List<ContributionDTO>> getContributions(@RequestAttribute String userId) {
        log.info("获取用户贡献日历数据: userId={}", userId);
        List<ContributionDTO> contributions = userProfileService.getContributions(userId);
        return ApiResponse.success(contributions);
    }

    /**
     * 获取用户最近活动
     * GET /api/user/profile/recent-activities?limit=10
     */
    @GetMapping("/recent-activities")
    public ApiResponse<List<UserRecentActivityDTO>> getRecentActivities(
            @RequestAttribute String userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取用户最近活动: userId={}, limit={}", userId, limit);
        List<UserRecentActivityDTO> activities = userProfileService.getRecentActivities(userId, limit);
        return ApiResponse.success(activities);
    }
}
