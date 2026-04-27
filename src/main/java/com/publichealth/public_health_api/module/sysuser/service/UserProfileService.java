package com.publichealth.public_health_api.module.sysuser.service;

import com.publichealth.public_health_api.module.sysuser.dto.*;

import java.util.List;

/**
 * 用户个人中心Service接口
 */
public interface UserProfileService {

    /**
     * 获取用户统计数据
     * @param userId 用户ID
     * @return 统计数据
     */
    UserProfileStatisticsDTO getStatistics(String userId);

    /**
     * 获取用户贡献日历数据（近365天）
     * @param userId 用户ID
     * @return 贡献日历数据
     */
    List<ContributionDTO> getContributions(String userId);

    /**
     * 获取用户最近活动
     * @param userId 用户ID
     * @param limit 返回条数限制
     * @return 最近活动列表
     */
    List<UserRecentActivityDTO> getRecentActivities(String userId, Integer limit);
}
