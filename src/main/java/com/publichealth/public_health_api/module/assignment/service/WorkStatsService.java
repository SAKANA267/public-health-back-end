package com.publichealth.public_health_api.module.assignment.service;

import com.publichealth.public_health_api.module.assignment.dto.WorkStatsResponse;

import java.util.List;

/**
 * 工作统计Service接口
 */
public interface WorkStatsService {

    /**
     * 获取审核组工作统计
     */
    WorkStatsResponse getGroupStats(String auditGroupId);

    /**
     * 获取所有审核组工作统计
     */
    List<WorkStatsResponse> getAllGroupStats();

    /**
     * 获取任务数最少的审核组
     */
    String getLeastLoadedGroupId();

    /**
     * 更新统计数据
     */
    void updateStats(String auditGroupId);
}
