package com.publichealth.public_health_api.module.sysuser.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.sysuser.dto.*;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import com.publichealth.public_health_api.module.sysuser.service.SysUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 * 包含用户相关的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    @Override
    @Transactional
    public SysUserDTO createUser(CreateUserRequest request) {
        log.info("创建用户: username={}", request.getUsername());

        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 3. 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : SysUser.UserRole.USER);
        user.setStatus(request.getStatus() != null ? request.getStatus() : SysUser.UserStatus.ACTIVE);
        user.setDataScope(request.getDataScope());
        user.setDeleted(false);

        // 4. 保存用户
        SysUser savedUser = userRepository.save(user);
        log.info("用户创建成功: id={}, username={}", savedUser.getId(), savedUser.getUsername());

        return SysUserDTO.fromEntity(savedUser);
    }

    @Override
    public SysUserDTO getUserById(String id) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return SysUserDTO.fromEntity(user);
    }

    @Override
    public SysUserDTO getUserByUsername(String username) {
        SysUser user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return SysUserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public SysUserDTO updateUser(String id, UpdateUserRequest request) {
        log.info("更新用户信息: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 更新邮箱时检查是否重复
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getDataScope())) {
            user.setDataScope(request.getDataScope());
        }

        SysUser updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: id={}", id);

        return SysUserDTO.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        log.info("删除用户: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 逻辑删除
        user.setDeleted(true);
        userRepository.save(user);

        log.info("用户已删除: id={}", id);
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<String> ids) {
        log.info("批量删除用户: count={}", ids.size());

        List<SysUser> users = userRepository.findAllById(ids);
        users.forEach(user -> user.setDeleted(true));
        userRepository.saveAll(users);

        log.info("批量删除成功: count={}", users.size());
    }

    // ============================================
    // 查询操作
    // ============================================

    @Override
    public PageResult<SysUserDTO> getUserList(UserQueryRequest request) {
        log.info("查询用户列表: page={}, size={}, keyword={}, role={}, status={}",
                request.getPage(), request.getSize(), request.getKeyword(),
                request.getRole(), request.getStatus());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,  // Spring Data JPA 页码从0开始
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        Page<SysUser> page;

        // 根据条件查询
        if (StringUtils.hasText(request.getKeyword())) {
            // 关键词搜索
            List<SysUser> users = userRepository.searchUsers(request.getKeyword());
            page = createPageFromList(users, pageable);
        } else if (request.getRole() != null) {
            // 按角色筛选
            page = userRepository.findByRoleAndDeletedFalse(request.getRole(), pageable);
        } else if (request.getStatus() != null) {
            // 按状态筛选
            page = userRepository.findByStatusAndDeletedFalse(request.getStatus(), pageable);
        } else if (Boolean.TRUE.equals(request.getIncludeDeleted())) {
            // 包含已删除用户
            page = userRepository.findAll(pageable);
        } else {
            // 默认查询未删除的用户
            page = userRepository.findAll(pageable);
        }

        // 转换为DTO
        List<SysUserDTO> dtoList = page.getContent().stream()
                .map(SysUserDTO::fromEntity)
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                dtoList
        );
    }

    @Override
    public List<SysUserDTO> searchUsers(String keyword) {
        List<SysUser> users = userRepository.searchUsers(keyword);
        return users.stream()
                .map(SysUserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysUserDTO> getUsersByRole(SysUser.UserRole role) {
        List<SysUser> users = userRepository.findByRoleAndDeletedFalse(role);
        return users.stream()
                .map(SysUserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // 状态管理
    // ============================================

    @Override
    @Transactional
    public void activateUser(String id) {
        log.info("启用用户: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setStatus(SysUser.UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(String id) {
        log.info("停用用户: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setStatus(SysUser.UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<String> ids, SysUser.UserStatus status) {
        log.info("批量更新用户状态: count={}, status={}", ids.size(), status);

        int updated = userRepository.batchUpdateStatus(ids, status);
        log.info("批量更新状态成功: count={}", updated);
    }

    // ============================================
    // 密码管理
    // ============================================

    @Override
    @Transactional
    public void changePassword(String id, ChangePasswordRequest request) {
        log.info("修改密码: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        // 设置新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("密码修改成功: id={}", id);
    }

    @Override
    @Transactional
    public void resetPassword(String id, String newPassword) {
        log.info("重置密码: id={}", id);

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("密码重置成功: id={}", id);
    }

    // ============================================
    // 登录相关
    // ============================================

    @Override
    public SysUser validateUser(String username, String password) {
        log.info("验证用户: username={}", username);

        return userRepository.findByUsernameAndDeletedFalse(username)
                .filter(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new BusinessException("用户名或密码错误");
                    }
                    return true;
                })
                .filter(user -> {
                    if (user.getStatus() != SysUser.UserStatus.ACTIVE) {
                        throw new BusinessException("用户已被禁用");
                    }
                    return true;
                })
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
    }

    @Override
    @Transactional
    public void updateLastLoginTime(String userId) {
        userRepository.updateLastLogin(userId, LocalDateTime.now());
    }

    // ============================================
    // 存在性检查
    // ============================================

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsernameExcludeId(String username, String excludeId) {
        return userRepository.existsByUsernameAndIdNot(username, excludeId);
    }

    // ============================================
    // 私有辅助方法
    // ============================================

    /**
     * 从List创建Page对象 (用于搜索结果分页)
     */
    private <T> org.springframework.data.domain.Page<T> createPageFromList(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());

        if (start >= list.size()) {
            return org.springframework.data.domain.Page.empty();
        }

        return new org.springframework.data.domain.PageImpl<>(
                list.subList(start, end),
                pageable,
                list.size()
        );
    }
}
