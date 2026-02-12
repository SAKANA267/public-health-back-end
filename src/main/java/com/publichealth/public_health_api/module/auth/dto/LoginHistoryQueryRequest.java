package com.publichealth.public_health_api.module.auth.dto;

import com.publichealth.public_health_api.module.auth.entity.LoginHistory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录历史查询请求DTO
 * 用于分页查询登录历史
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryQueryRequest {

    /**
     * 用户ID（必填）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 登录状态（可选）
     * 不传则查询所有状态
     */
    private LoginHistory.LoginStatus status;

    /**
     * 开始时间（可选）
     * 用于时间范围查询
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（可选）
     * 用于时间范围查询
     */
    private LocalDateTime endTime;

    /**
     * 页码（从1开始）
     * 默认为1
     */
    @Min(value = 1, message = "页码必须大于0")
    private int page = 1;

    /**
     * 每页大小
     * 默认为10
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private int size = 10;
}
