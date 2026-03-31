package com.publichealth.public_health_api.module.auditgroup.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.auditgroup.dto.*;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroupMember;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupMemberRepository;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupRepository;
import com.publichealth.public_health_api.module.auditgroup.service.AuditGroupService;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核组服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditGroupServiceImpl implements AuditGroupService {

    private final AuditGroupRepository auditGroupRepository;
    private final AuditGroupMemberRepository auditGroupMemberRepository;
    private final SysUserRepository sysUserRepository;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    @Override
    @Transactional
    public AuditGroupDTO createGroup(CreateAuditGroupRequest request) {
        log.info("创建审核组: groupName={}, groupCode={}", request.getGroupName(), request.getGroupCode());

        // 1. 检查组名是否已存在
        if (auditGroupRepository.existsByGroupName(request.getGroupName())) {
            throw new BusinessException("审核组名称已存在");
        }

        // 2. 检查组编码是否已存在
        if (auditGroupRepository.existsByGroupCode(request.getGroupCode())) {
            throw new BusinessException("审核组编码已存在");
        }

        // 3. 验证组长是否存在
        if (StringUtils.hasText(request.getLeaderId())) {
            if (!sysUserRepository.existsById(request.getLeaderId())) {
                throw new BusinessException("指定的组长用户不存在");
            }
        }

        // 4. 创建审核组实体
        AuditGroup group = new AuditGroup();
        group.setGroupName(request.getGroupName());
        group.setGroupCode(request.getGroupCode());
        group.setDescription(request.getDescription());
        group.setLeaderId(request.getLeaderId());
        group.setStatus(request.getStatus() != null ? request.getStatus() : AuditGroup.AuditGroupStatus.ACTIVE);
        group.setDeleted(false);

        // 5. 保存审核组
        AuditGroup savedGroup = auditGroupRepository.save(group);
        log.info("审核组创建成功: id={}, groupName={}", savedGroup.getId(), savedGroup.getGroupName());

        AuditGroupDTO dto = AuditGroupDTO.fromEntity(savedGroup);
        dto.setMemberCount(0);

        // 设置组长姓名
        if (StringUtils.hasText(savedGroup.getLeaderId())) {
            sysUserRepository.findById(savedGroup.getLeaderId()).ifPresent(user -> {
                dto.setLeaderName(user.getName());
            });
        }

        return dto;
    }

    @Override
    public AuditGroupDTO getGroupById(String id) {
        AuditGroup group = auditGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (group.getDeleted()) {
            throw new BusinessException("审核组不存在");
        }

        AuditGroupDTO dto = AuditGroupDTO.fromEntity(group);
        dto.setMemberCount((int) auditGroupMemberRepository.countByGroupId(id));

        // 设置组长姓名
        if (StringUtils.hasText(group.getLeaderId())) {
            sysUserRepository.findById(group.getLeaderId()).ifPresent(user -> {
                dto.setLeaderName(user.getName());
            });
        }

        return dto;
    }

    @Override
    public AuditGroupDTO getGroupByCode(String groupCode) {
        AuditGroup group = auditGroupRepository.findByGroupCodeAndDeletedFalse(groupCode)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        AuditGroupDTO dto = AuditGroupDTO.fromEntity(group);
        dto.setMemberCount((int) auditGroupMemberRepository.countByGroupId(groupCode));

        // 设置组长姓名
        if (StringUtils.hasText(group.getLeaderId())) {
            sysUserRepository.findById(group.getLeaderId()).ifPresent(user -> {
                dto.setLeaderName(user.getName());
            });
        }

        return dto;
    }

    @Override
    @Transactional
    public AuditGroupDTO updateGroup(String id, UpdateAuditGroupRequest request) {
        log.info("更新审核组信息: id={}", id);

        AuditGroup group = auditGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (group.getDeleted()) {
            throw new BusinessException("审核组不存在");
        }

        // 更新组名时检查是否重复
        if (StringUtils.hasText(request.getGroupName()) && !request.getGroupName().equals(group.getGroupName())) {
            if (auditGroupRepository.existsByGroupName(request.getGroupName())) {
                throw new BusinessException("审核组名称已存在");
            }
            group.setGroupName(request.getGroupName());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getDescription())) {
            group.setDescription(request.getDescription());
        }

        // 更新组长时验证用户是否存在
        if (request.getLeaderId() != null) {
            if (!request.getLeaderId().equals(group.getLeaderId())) {
                if (StringUtils.hasText(request.getLeaderId()) && !sysUserRepository.existsById(request.getLeaderId())) {
                    throw new BusinessException("指定的组长用户不存在");
                }
                group.setLeaderId(request.getLeaderId());
            }
        }

        if (request.getStatus() != null) {
            group.setStatus(request.getStatus());
        }

        AuditGroup updatedGroup = auditGroupRepository.save(group);
        log.info("审核组信息更新成功: id={}", id);

        return getGroupById(id);
    }

    @Override
    @Transactional
    public void deleteGroup(String id) {
        log.info("删除审核组: id={}", id);

        AuditGroup group = auditGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        // 逻辑删除
        group.setDeleted(true);
        auditGroupRepository.save(group);

        // 删除所有成员关联
        auditGroupMemberRepository.deleteByGroupId(id);

        log.info("审核组已删除: id={}", id);
    }

    // ============================================
    // 查询操作
    // ============================================

    @Override
    public PageResult<AuditGroupDTO> getGroupList(AuditGroupQueryRequest request) {
        log.info("查询审核组列表: page={}, size={}, keyword={}, status={}",
                request.getPage(), request.getSize(), request.getKeyword(), request.getStatus());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        // 统一条件查询
        Page<AuditGroup> page = auditGroupRepository.findByConditions(
                request.getKeyword(),
                request.getStatus(),
                pageable
        );

        // 转换为DTO
        List<AuditGroupDTO> dtoList = page.getContent().stream()
                .map(group -> {
                    AuditGroupDTO dto = AuditGroupDTO.fromEntity(group);
                    dto.setMemberCount((int) auditGroupMemberRepository.countByGroupId(group.getId()));
                    // 设置组长姓名
                    if (StringUtils.hasText(group.getLeaderId())) {
                        sysUserRepository.findById(group.getLeaderId()).ifPresent(user -> {
                            dto.setLeaderName(user.getName());
                        });
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                dtoList
        );
    }

    @Override
    public List<AuditGroupDTO> searchGroups(String keyword) {
        List<AuditGroup> groups = auditGroupRepository.searchGroups(keyword);
        return groups.stream()
                .map(group -> {
                    AuditGroupDTO dto = AuditGroupDTO.fromEntity(group);
                    dto.setMemberCount((int) auditGroupMemberRepository.countByGroupId(group.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditGroupDTO> getActiveGroups() {
        List<AuditGroup> groups = auditGroupRepository.findByStatusAndDeletedFalse(AuditGroup.AuditGroupStatus.ACTIVE);
        return groups.stream()
                .map(AuditGroupDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // 状态管理
    // ============================================

    @Override
    @Transactional
    public void activateGroup(String id) {
        log.info("启用审核组: id={}", id);

        AuditGroup group = auditGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        group.setStatus(AuditGroup.AuditGroupStatus.ACTIVE);
        auditGroupRepository.save(group);
    }

    @Override
    @Transactional
    public void deactivateGroup(String id) {
        log.info("停用审核组: id={}", id);

        AuditGroup group = auditGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        group.setStatus(AuditGroup.AuditGroupStatus.INACTIVE);
        auditGroupRepository.save(group);
    }

    // ============================================
    // 成员管理
    // ============================================

    @Override
    @Transactional
    public void addMembers(AddMemberRequest request) {
        log.info("添加审核组成员: groupId, userIds={}", request.getGroupId(), request.getUserIds());

        // 验证审核组是否存在
        AuditGroup group = auditGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        if (group.getDeleted()) {
            throw new BusinessException("审核组不存在");
        }

        // 添加成员
        for (String userId : request.getUserIds()) {
            // 验证用户是否存在
            if (!sysUserRepository.existsById(userId)) {
                throw new BusinessException("用户不存在: " + userId);
            }

            // 检查是否已经是成员
            if (!auditGroupMemberRepository.existsByGroupIdAndUserId(request.getGroupId(), userId)) {
                AuditGroupMember member = new AuditGroupMember();
                member.setGroupId(request.getGroupId());
                member.setUserId(userId);
                auditGroupMemberRepository.save(member);
            }
        }

        log.info("添加审核组成员成功: groupId", request.getGroupId());
    }

    @Override
    @Transactional
    public void removeMembers(RemoveMemberRequest request) {
        log.info("移除审核组成员: groupId, userIds={}", request.getGroupId(), request.getUserIds());

        // 验证审核组是否存在
        AuditGroup group = auditGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        for (String userId : request.getUserIds()) {
            auditGroupMemberRepository.deleteByGroupIdAndUserId(request.getGroupId(), userId);
        }

        log.info("移除审核组成员成功: groupId", request.getGroupId());
    }

    @Override
    public List<GroupMemberDTO> getGroupMembers(String groupId) {
        log.info("获取审核组成员: groupId={}", groupId);

        // 验证审核组是否存在
        if (!auditGroupRepository.existsById(groupId)) {
            throw new BusinessException("审核组不存在");
        }

        List<AuditGroupMember> members = auditGroupMemberRepository.findByGroupId(groupId);

        return members.stream()
                .map(member -> {
                    SysUser user = sysUserRepository.findById(member.getUserId()).orElse(null);
                    return GroupMemberDTO.fromUser(user, member.getJoinTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserGroupDTO> getUserGroups(String userId) {
        log.info("获取用户所属审核组: userId={}", userId);

        // 验证用户是否存在
        if (!sysUserRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }

        List<AuditGroupMember> memberships = auditGroupMemberRepository.findByUserId(userId);

        return memberships.stream()
                .map(member -> {
                    AuditGroup group = auditGroupRepository.findById(member.getGroupId()).orElse(null);
                    if (group != null && !group.getDeleted()) {
                        boolean isLeader = group.getLeaderId() != null && group.getLeaderId().equals(userId);
                        return UserGroupDTO.fromGroup(group, isLeader, member.getJoinTime());
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setGroupLeader(String groupId, String leaderId) {
        log.info("设置审核组组长: groupId={}, leaderId={}", groupId, leaderId);

        AuditGroup group = auditGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("审核组不存在"));

        // 验证用户是否存在
        if (!sysUserRepository.existsById(leaderId)) {
            throw new BusinessException("指定的用户不存在");
        }

        // 检查用户是否在组中
        if (!auditGroupMemberRepository.existsByGroupIdAndUserId(groupId, leaderId)) {
            throw new BusinessException("指定的用户不在该审核组中");
        }

        group.setLeaderId(leaderId);
        auditGroupRepository.save(group);

        log.info("审核组组长设置成功: groupId={}, leaderId={}", groupId, leaderId);
    }

    // ============================================
    // 存在性检查
    // ============================================

    @Override
    public boolean existsByGroupName(String groupName) {
        return auditGroupRepository.existsByGroupNameAndDeletedFalse(groupName);
    }

    @Override
    public boolean existsByGroupCode(String groupCode) {
        return auditGroupRepository.existsByGroupCodeAndDeletedFalse(groupCode);
    }

    @Override
    public boolean existsByGroupNameExcludeId(String groupName, String excludeId) {
        return auditGroupRepository.existsByGroupNameAndIdNot(groupName, excludeId);
    }

    @Override
    public boolean existsByGroupCodeExcludeId(String groupCode, String excludeId) {
        return auditGroupRepository.existsByGroupCodeAndIdNot(groupCode, excludeId);
    }
}
