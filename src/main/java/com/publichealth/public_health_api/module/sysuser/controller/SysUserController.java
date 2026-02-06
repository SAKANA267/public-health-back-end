package com.publichealth.public_health_api.module.sysuser.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import com.publichealth.public_health_api.module.sysuser.dto.*;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制器
 * 处理用户相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping
    @OperationLog(module = "用户管理", operationType = OperationType.CREATE, description = "创建用户")
    public ApiResponse<SysUserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("收到创建用户请求: username={}", request.getUsername());
        SysUserDTO user = userService.createUser(request);
        return ApiResponse.success("用户创建成功", user);
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<SysUserDTO> getUserById(@PathVariable String id) {
        log.info("获取用户信息: id={}", id);
        SysUserDTO user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    /**
     * 根据用户名获取用户
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ApiResponse<SysUserDTO> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户: username={}", username);
        SysUserDTO user = userService.getUserByUsername(username);
        return ApiResponse.success(user);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @OperationLog(module = "用户管理", operationType = OperationType.UPDATE, description = "更新用户信息")
    public ApiResponse<SysUserDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("更新用户信息: id={}", id);
        SysUserDTO user = userService.updateUser(id, request);
        return ApiResponse.success("用户信息更新成功", user);
    }

    /**
     * 删除用户 (逻辑删除)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "用户管理", operationType = OperationType.DELETE, description = "删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return ApiResponse.success("用户已删除");
    }

    /**
     * 批量删除用户
     * DELETE /api/users/batch
     */
    @DeleteMapping("/batch")
    @OperationLog(module = "用户管理", operationType = OperationType.DELETE, description = "批量删除用户")
    public ApiResponse<Void> batchDeleteUsers(@RequestBody List<String> ids) {
        log.info("批量删除用户: count={}", ids.size());
        userService.batchDeleteUsers(ids);
        return ApiResponse.success("批量删除成功");
    }

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询用户列表
     * GET /api/users?page=1&size=10&keyword=xxx&role=USER&status=ACTIVE
     */
    @GetMapping
    public ApiResponse<PageResult<SysUserDTO>> getUserList(UserQueryRequest request) {
        log.info("查询用户列表: {}", request);
        PageResult<SysUserDTO> result = userService.getUserList(request);
        return ApiResponse.success(result);
    }

    /**
     * 搜索用户
     * GET /api/users/search?keyword=xxx
     */
    @GetMapping("/search")
    public ApiResponse<List<SysUserDTO>> searchUsers(@RequestParam String keyword) {
        log.info("搜索用户: keyword={}", keyword);
        List<SysUserDTO> users = userService.searchUsers(keyword);
        return ApiResponse.success(users);
    }

    /**
     * 根据角色获取用户列表
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ApiResponse<List<SysUserDTO>> getUsersByRole(@PathVariable SysUser.UserRole role) {
        log.info("获取角色用户列表: role={}", role);
        List<SysUserDTO> users = userService.getUsersByRole(role);
        return ApiResponse.success(users);
    }

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用用户
     * PUT /api/users/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ApiResponse<Void> activateUser(@PathVariable String id) {
        log.info("启用用户: id={}", id);
        userService.activateUser(id);
        return ApiResponse.success("用户已启用");
    }

    /**
     * 停用用户
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateUser(@PathVariable String id) {
        log.info("停用用户: id={}", id);
        userService.deactivateUser(id);
        return ApiResponse.success("用户已停用");
    }

    /**
     * 批量更新用户状态
     * PUT /api/users/batch/status
     */
    @PutMapping("/batch/status")
    public ApiResponse<Void> batchUpdateStatus(
            @RequestBody List<String> ids,
            @RequestParam SysUser.UserStatus status) {
        log.info("批量更新用户状态: count={}, status={}", ids.size(), status);
        userService.batchUpdateStatus(ids, status);
        return ApiResponse.success("批量更新状态成功");
    }

    // ============================================
    // 密码管理
    // ============================================

    /**
     * 修改密码
     * PUT /api/users/{id}/password
     */
    @PutMapping("/{id}/password")
    @OperationLog(module = "用户管理", operationType = OperationType.UPDATE, description = "修改密码", logParams = false)
    public ApiResponse<Void> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("修改密码: id={}", id);
        userService.changePassword(id, request);
        return ApiResponse.success("密码修改成功");
    }

    /**
     * 重置密码 (管理员操作)
     * PUT /api/users/{id}/password/reset
     */
    @PutMapping("/{id}/password/reset")
    public ApiResponse<Void> resetPassword(
            @PathVariable String id,
            @RequestParam String newPassword) {
        log.info("重置密码: id={}", id);
        userService.resetPassword(id, newPassword);
        return ApiResponse.success("密码重置成功");
    }

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查用户名是否存在
     * GET /api/users/check/username?username=xxx
     */
    @GetMapping("/check/username")
    public ApiResponse<Boolean> checkUsernameExists(
            @RequestParam String username,
            @RequestParam(required = false) String excludeId) {
        boolean exists = StringUtils.hasText(excludeId)
                ? userService.existsByUsernameExcludeId(username, excludeId)
                : userService.existsByUsername(username);
        return ApiResponse.success(exists);
    }

    /**
     * 检查邮箱是否存在
     * GET /api/users/check/email?email=xxx
     */
    @GetMapping("/check/email")
    public ApiResponse<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(exists);
    }
}
