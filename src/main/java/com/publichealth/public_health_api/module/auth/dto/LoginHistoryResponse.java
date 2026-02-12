package com.publichealth.public_health_api.module.auth.dto;

import com.publichealth.public_health_api.module.auth.entity.LoginHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录历史响应DTO
 * 用于返回登录历史数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryResponse {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * 登录状态
     */
    private LoginHistory.LoginStatus status;

    /**
     * 失败原因
     * 仅当status=FAILURE时有值
     */
    private String failReason;
}
