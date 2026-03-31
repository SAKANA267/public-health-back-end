package com.publichealth.public_health_api.module.auditgroup.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.auditgroup.dto.*;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;

import java.util.List;

/**
 * 审核组服务接口
 */
public interface AuditGroupService {

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建审核组
     */
    AuditGroupDTO createGroup(CreateAuditGroupRequest request);

    /**
     * 根据ID获取审核组
     */
    AuditGroupDTO getGroupById(String id);

    /**
     * 根据组编码获取审核组
     */
    AuditGroupDTO getGroupByCode(String groupCode);

    /**
     * 更新审核组信息
     */
    AuditGroupDTO updateGroup(String id, UpdateAuditGroupRequest request);

    /**
     * 删除审核组 (逻辑删除)
     */
    void deleteGroup(String id);

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询审核组列表
     */
    PageResult<AuditGroupDTO> getGroupList(AuditGroupQueryRequest request);

    /**
     * 搜索审核组
     */
    List<AuditGroupDTO> searchGroups(String keyword);

    /**
     * 获取所有启用的审核组
     */
    List<AuditGroupDTO> getActiveGroups();

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用审核组
     */
    void activateGroup(String id);

    /**
     * 停用审核组
     */
    void deactivateGroup(String id);

    // ============================================
    // 成员管理
    // ============================================

    /**
     * 添加组成员
     */
    void addMembers(AddMemberRequest request);

    /**
     * 移除组成员
     */
    void removeMembers(RemoveMemberRequest request);

    /**
     * 获取审核组的所有成员
     */
    List<GroupMemberDTO> getGroupMembers(String groupId);

    /**
     * 获取用户所属的所有审核组
     */
    List<UserGroupDTO> getUserGroups(String userId);

    /**
     * 设置审核组组长
     */
    void setGroupLeader(String groupId, String leaderId);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查组名是否存在
     */
    boolean existsByGroupName(String groupName);

    /**
     * 检查组编码是否存在
     */
    boolean existsByGroupCode(String groupCode);

    /**
     * 检查组名是否存在 (排除指定ID)
     */
    boolean existsByGroupNameExcludeId(String groupName, String excludeId);

    /**
     * 检查组编码是否存在 (排除指定ID)
     */
    boolean existsByGroupCodeExcludeId(String groupCode, String excludeId);
}
