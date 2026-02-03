package com.publichealth.public_health_api.module.sysuser.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.sysuser.dto.*;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;

import java.util.List;

/**
 * 系统用户服务接口
 * 定义用户相关的业务操作
 */
public interface SysUserService {

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建用户
     * @param request 创建用户请求
     * @return 创建的用户DTO
     */
    SysUserDTO createUser(CreateUserRequest request);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户DTO
     */
    SysUserDTO getUserById(String id);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户DTO
     */
    SysUserDTO getUserByUsername(String username);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param request 更新用户请求
     * @return 更新后的用户DTO
     */
    SysUserDTO updateUser(String id, UpdateUserRequest request);

    /**
     * 删除用户 (逻辑删除)
     * @param id 用户ID
     */
    void deleteUser(String id);

    /**
     * 批量删除用户 (逻辑删除)
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(List<String> ids);

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询用户列表
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<SysUserDTO> getUserList(UserQueryRequest request);

    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 用户DTO列表
     */
    List<SysUserDTO> searchUsers(String keyword);

    /**
     * 根据角色获取用户列表
     * @param role 用户角色
     * @return 用户DTO列表
     */
    List<SysUserDTO> getUsersByRole(SysUser.UserRole role);

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用用户
     * @param id 用户ID
     */
    void activateUser(String id);

    /**
     * 停用用户
     * @id 用户ID
     */
    void deactivateUser(String id);

    /**
     * 批量更新用户状态
     * @param ids 用户ID列表
     * @param status 目标状态
     */
    void batchUpdateStatus(List<String> ids, SysUser.UserStatus status);

    // ============================================
    // 密码管理
    // ============================================

    /**
     * 修改密码
     * @param id 用户ID
     * @param request 修改密码请求
     */
    void changePassword(String id, ChangePasswordRequest request);

    /**
     * 重置密码 (管理员操作)
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(String id, String newPassword);

    // ============================================
    // 登录相关
    // ============================================

    /**
     * 验证用户密码
     * @param username 用户名
     * @param password 密码
     * @return 验证成功返回用户实体，失败返回null
     */
    SysUser validateUser(String username, String password);

    /**
     * 更新最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(String userId);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查用户名是否存在 (排除指定ID)
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByUsernameExcludeId(String username, String excludeId);
}
