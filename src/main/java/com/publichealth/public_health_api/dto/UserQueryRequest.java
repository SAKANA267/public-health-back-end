package com.publichealth.public_health_api.dto;

import com.publichealth.public_health_api.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询请求DTO
 * 用于用户列表查询和筛选
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {

    /**
     * 页码 (从1开始)
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
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
     * 是否包含已删除用户
     */
    private Boolean includeDeleted = false;
}
