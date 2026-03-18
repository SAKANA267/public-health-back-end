package com.publichealth.public_health_api.module.sysuser.dto;

import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户查询请求DTO
 * 用于用户列表查询和筛选
 * 统一查询标准：startTime/endTime 使用 LocalDateTime 类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {

    /**
     * 页码 (从1开始)
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    /**
     * 搜索关键词 (用户名或姓名)
     */
    private String keyword;

    /**
     * 角色筛选
     */
    private SysUser.UserRole role;

    /**
     * 状态筛选
     */
    private SysUser.UserStatus status;

    /**
     * 开始时间 (查询创建时间范围)
     */
    private LocalDateTime startTime;

    /**
     * 结束时间 (查询创建时间范围)
     */
    private LocalDateTime endTime;

    /**
     * 是否包含已删除用户
     */
    private Boolean includeDeleted = false;
}
