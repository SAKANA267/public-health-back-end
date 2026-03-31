package com.publichealth.public_health_api.module.auditgroup.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.auditgroup.dto.*;
import com.publichealth.public_health_api.module.auditgroup.service.AuditGroupService;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审核组控制器
 * 处理审核组相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/audit-groups")
@RequiredArgsConstructor
public class AuditGroupController {

    private final AuditGroupService auditGroupService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建审核组
     * POST /api/audit-groups
     */
    @PostMapping
    @OperationLog(module = "审核组管理", operationType = OperationType.CREATE, description = "创建审核组")
    public ApiResponse<AuditGroupDTO> createGroup(@Valid @RequestBody CreateAuditGroupRequest request) {
        log.info("收到创建审核组请求: groupName={}", request.getGroupName());
        AuditGroupDTO group = auditGroupService.createGroup(request);
        return ApiResponse.success("审核组创建成功", group);
    }

    /**
     * 根据ID获取审核组
     * GET /api/audit-groups/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<AuditGroupDTO> getGroupById(@PathVariable String id) {
        log.info("获取审核组信息: id={}", id);
        AuditGroupDTO group = auditGroupService.getGroupById(id);
        return ApiResponse.success(group);
    }

    /**
     * 根据组编码获取审核组
     * GET /api/audit-groups/code/{groupCode}
     */
    @GetMapping("/code/{groupCode}")
    public ApiResponse<AuditGroupDTO> getGroupByCode(@PathVariable String groupCode) {
        log.info("根据组编码获取审核组: groupCode={}", groupCode);
        AuditGroupDTO group = auditGroupService.getGroupByCode(groupCode);
        return ApiResponse.success(group);
    }

    /**
     * 更新审核组信息
     * PUT /api/audit-groups/{id}
     */
    @PutMapping("/{id}")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "更新审核组信息")
    public ApiResponse<AuditGroupDTO> updateGroup(
            @PathVariable String id,
            @Valid @RequestBody UpdateAuditGroupRequest request) {
        log.info("更新审核组信息: id={}", id);
        AuditGroupDTO group = auditGroupService.updateGroup(id, request);
        return ApiResponse.success("审核组信息更新成功", group);
    }

    /**
     * 删除审核组 (逻辑删除)
     * DELETE /api/audit-groups/{id}
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "审核组管理", operationType = OperationType.DELETE, description = "删除审核组")
    public ApiResponse<Void> deleteGroup(@PathVariable String id) {
        log.info("删除审核组: id={}", id);
        auditGroupService.deleteGroup(id);
        return ApiResponse.success("审核组已删除");
    }

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询审核组列表
     * GET /api/audit-groups?page=1&size=10&keyword=xxx&status=ACTIVE
     */
    @GetMapping
    public ApiResponse<PageResult<AuditGroupDTO>> getGroupList(AuditGroupQueryRequest request) {
        log.info("查询审核组列表: {}", request);
        PageResult<AuditGroupDTO> result = auditGroupService.getGroupList(request);
        return ApiResponse.success(result);
    }

    /**
     * 搜索审核组
     * GET /api/audit-groups/search?keyword=xxx
     */
    @GetMapping("/search")
    public ApiResponse<List<AuditGroupDTO>> searchGroups(@RequestParam String keyword) {
        log.info("搜索审核组: keyword={}", keyword);
        List<AuditGroupDTO> groups = auditGroupService.searchGroups(keyword);
        return ApiResponse.success(groups);
    }

    /**
     * 获取所有启用的审核组
     * GET /api/audit-groups/active
     */
    @GetMapping("/active")
    public ApiResponse<List<AuditGroupDTO>> getActiveGroups() {
        log.info("获取所有启用的审核组");
        List<AuditGroupDTO> groups = auditGroupService.getActiveGroups();
        return ApiResponse.success(groups);
    }

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用审核组
     * PUT /api/audit-groups/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "启用审核组")
    public ApiResponse<Void> activateGroup(@PathVariable String id) {
        log.info("启用审核组: id={}", id);
        auditGroupService.activateGroup(id);
        return ApiResponse.success("审核组已启用");
    }

    /**
     * 停用审核组
     * PUT /api/audit-groups/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "停用审核组")
    public ApiResponse<Void> deactivateGroup(@PathVariable String id) {
        log.info("停用审核组: id={}", id);
        auditGroupService.deactivateGroup(id);
        return ApiResponse.success("审核组已停用");
    }

    // ============================================
    // 成员管理
    // ============================================

    /**
     * 添加组成员
     * POST /api/audit-groups/members/add
     */
    @PostMapping("/members/add")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "添加审核组成员")
    public ApiResponse<Void> addMembers(@Valid @RequestBody AddMemberRequest request) {
        log.info("添加审核组成员: groupId={}, userIds={}", request.getGroupId(), request.getUserIds());
        auditGroupService.addMembers(request);
        return ApiResponse.success("成员添加成功");
    }

    /**
     * 移除组成员
     * POST /api/audit-groups/members/remove
     */
    @PostMapping("/members/remove")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "移除审核组成员")
    public ApiResponse<Void> removeMembers(@Valid @RequestBody RemoveMemberRequest request) {
        log.info("移除审核组成员: groupId={}, userIds={}", request.getGroupId(), request.getUserIds());
        auditGroupService.removeMembers(request);
        return ApiResponse.success("成员移除成功");
    }

    /**
     * 获取审核组的所有成员
     * GET /api/audit-groups/{id}/members
     */
    @GetMapping("/{id}/members")
    public ApiResponse<List<GroupMemberDTO>> getGroupMembers(@PathVariable String id) {
        log.info("获取审核组成员: groupId={}", id);
        List<GroupMemberDTO> members = auditGroupService.getGroupMembers(id);
        return ApiResponse.success(members);
    }

    /**
     * 获取用户所属的所有审核组
     * GET /api/audit-groups/user/{userId}/groups
     */
    @GetMapping("/user/{userId}/groups")
    public ApiResponse<List<UserGroupDTO>> getUserGroups(@PathVariable String userId) {
        log.info("获取用户所属审核组: userId={}", userId);
        List<UserGroupDTO> groups = auditGroupService.getUserGroups(userId);
        return ApiResponse.success(groups);
    }

    /**
     * 设置审核组组长
     * PUT /api/audit-groups/{id}/leader
     */
    @PutMapping("/{id}/leader")
    @OperationLog(module = "审核组管理", operationType = OperationType.UPDATE, description = "设置审核组组长")
    public ApiResponse<Void> setGroupLeader(
            @PathVariable String id,
            @RequestParam String leaderId) {
        log.info("设置审核组组长: groupId={}, leaderId={}", id, leaderId);
        auditGroupService.setGroupLeader(id, leaderId);
        return ApiResponse.success("组长设置成功");
    }

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查组名是否存在
     * GET /api/audit-groups/check/name?groupName=xxx&excludeId=xxx
     */
    @GetMapping("/check/name")
    public ApiResponse<Boolean> checkGroupNameExists(
            @RequestParam String groupName,
            @RequestParam(required = false) String excludeId) {
        boolean exists = excludeId != null
                ? auditGroupService.existsByGroupNameExcludeId(groupName, excludeId)
                : auditGroupService.existsByGroupName(groupName);
        return ApiResponse.success(exists);
    }

    /**
     * 检查组编码是否存在
     * GET /api/audit-groups/check/code?groupCode=xxx&excludeId=xxx
     */
    @GetMapping("/check/code")
    public ApiResponse<Boolean> checkGroupCodeExists(
            @RequestParam String groupCode,
            @RequestParam(required = false) String excludeId) {
        boolean exists = excludeId != null
                ? auditGroupService.existsByGroupCodeExcludeId(groupCode, excludeId)
                : auditGroupService.existsByGroupCode(groupCode);
        return ApiResponse.success(exists);
    }
}
