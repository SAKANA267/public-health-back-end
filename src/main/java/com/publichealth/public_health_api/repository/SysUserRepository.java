package com.publichealth.public_health_api.repository;

import com.publichealth.public_health_api.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 系统用户数据访问层
 * 继承 JpaRepository 获得基础 CRUD 功能
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, String> {

    // ============================================
    // 基础查询方法 (Spring Data JPA 自动实现)
    // ============================================

    /**
     * 根据用户名查询用户
     * 方法名规则: findBy + 字段名
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * 根据用户名查询未删除的用户
     */
    Optional<SysUser> findByUsernameAndDeletedFalse(String username);

    /**
     * 根据邮箱查询用户
     */
    Optional<SysUser> findByEmail(String email);

    /**
     * 根据邮箱查询未删除的用户
     */
    Optional<SysUser> findByEmailAndDeletedFalse(String email);

    /**
     * 根据手机号查询用户
     */
    Optional<SysUser> findByPhone(String phone);

    // ============================================
    // 角色相关查询
    // ============================================

    /**
     * 根据角色查询用户列表
     */
    List<SysUser> findByRole(SysUser.UserRole role);

    /**
     * 根据角色查询未删除的用户列表
     */
    List<SysUser> findByRoleAndDeletedFalse(SysUser.UserRole role);

    /**
     * 根据角色查询未删除的用户列表 (分页)
     */
    Page<SysUser> findByRoleAndDeletedFalse(SysUser.UserRole role, Pageable pageable);

    /**
     * 根据角色列表查询用户
     */
    List<SysUser> findByRoleIn(List<SysUser.UserRole> roles);

    // ============================================
    // 状态相关查询
    // ============================================

    /**
     * 根据状态查询用户列表
     */
    List<SysUser> findByStatus(SysUser.UserStatus status);

    /**
     * 查询所有激活且未删除的用户
     */
    List<SysUser> findByStatusAndDeletedFalse(SysUser.UserStatus status);

    /**
     * 查询所有激活且未删除的用户 (分页)
     */
    Page<SysUser> findByStatusAndDeletedFalse(SysUser.UserStatus status, Pageable pageable);

    // ============================================
    // 删除标记相关查询
    // ============================================

    /**
     * 查询所有未删除的用户
     */
    List<SysUser> findByDeletedFalse();

    /**
     * 查询所有已删除的用户
     */
    List<SysUser> findByDeletedTrue();

    // ============================================
    // 时间范围查询
    // ============================================

    /**
     * 查询指定时间之后创建的用户
     */
    List<SysUser> findByCreateTimeAfter(LocalDateTime dateTime);

    /**
     * 查询指定时间范围内登录的用户
     */
    List<SysUser> findByLastLoginBetween(LocalDateTime start, LocalDateTime end);

    // ============================================
    // 自定义 JPQL 查询
    // ============================================

    /**
     * 统计指定角色的用户数量
     */
    @Query("SELECT COUNT(u) FROM SysUser u WHERE u.role = :role AND u.deleted = false")
    long countByRoleAndDeletedFalse(@Param("role") SysUser.UserRole role);

    /**
     * 搜索用户 (用户名或姓名模糊匹配)
     */
    @Query("SELECT u FROM SysUser u WHERE u.deleted = false AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<SysUser> searchUsers(@Param("keyword") String keyword);

    /**
     * 查询用户及其完整信息 (使用 JOIN FETCH 解决 N+1 问题)
     */
    @Query("SELECT u FROM SysUser u WHERE u.id = :id AND u.deleted = false")
    Optional<SysUser> findUserByIdWithDetails(@Param("id") String id);

    // ============================================
    // 修改/删除操作 (需要 @Modifying 注解)
    // ============================================

    /**
     * 逻辑删除用户
     */
    @Query("UPDATE SysUser u SET u.deleted = true WHERE u.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int logicalDeleteById(@Param("id") String id);

    /**
     * 更新最后登录时间
     */
    @Query("UPDATE SysUser u SET u.lastLogin = :loginTime WHERE u.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int updateLastLogin(@Param("id") String id, @Param("loginTime") LocalDateTime loginTime);

    /**
     * 批量更新用户状态
     */
    @Query("UPDATE SysUser u SET u.status = :status WHERE u.id IN :ids")
    @org.springframework.data.jpa.repository.Modifying
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") SysUser.UserStatus status);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查用户名是否存在 (排除指定ID)
     * 用于更新时检查用户名是否与其他用户冲突
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM SysUser u " +
           "WHERE u.username = :username AND u.id != :id AND u.deleted = false")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") String id);
}
