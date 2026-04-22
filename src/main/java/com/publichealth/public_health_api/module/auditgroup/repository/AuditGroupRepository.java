package com.publichealth.public_health_api.module.auditgroup.repository;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 审核组数据访问层
 */
@Repository
public interface AuditGroupRepository extends JpaRepository<AuditGroup, String> {

    /**
     * 根据审核组名称查询
     */
    Optional<AuditGroup> findByGroupName(String groupName);

    /**
     * 根据审核组名称查询未删除的组
     */
    Optional<AuditGroup> findByGroupNameAndDeletedFalse(String groupName);

    /**
     * 根据审核组编码查询
     */
    Optional<AuditGroup> findByGroupCode(String groupCode);

    /**
     * 根据审核组编码查询未删除的组
     */
    Optional<AuditGroup> findByGroupCodeAndDeletedFalse(String groupCode);

    /**
     * 根据状态查询审核组列表
     */
    List<AuditGroup> findByStatus(AuditGroup.AuditGroupStatus status);

    /**
     * 根据状态查询未删除的审核组列表
     */
    List<AuditGroup> findByStatusAndDeletedFalse(AuditGroup.AuditGroupStatus status);

    /**
     * 查询所有未删除的审核组
     */
    List<AuditGroup> findByDeletedFalse();

    /**
     * 查询所有未删除的审核组 (分页)
     */
    Page<AuditGroup> findByDeletedFalse(Pageable pageable);

    /**
     * 搜索审核组 (组名或编码模糊匹配)
     */
    @Query("SELECT ag FROM AuditGroup ag WHERE ag.deleted = false AND " +
           "(LOWER(ag.groupName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ag.groupCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<AuditGroup> searchGroups(@Param("keyword") String keyword);

    /**
     * 统一条件查询 (支持多条件组合)
     */
    @Query("SELECT ag FROM AuditGroup ag WHERE " +
           "(:keyword IS NULL OR LOWER(ag.groupName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ag.groupCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR ag.status = :status) AND " +
           "ag.deleted = false")
    Page<AuditGroup> findByConditions(
            @Param("keyword") String keyword,
            @Param("status") AuditGroup.AuditGroupStatus status,
            Pageable pageable
    );

    /**
     * 检查组名是否存在
     */
    boolean existsByGroupName(String groupName);

    /**
     * 检查组名是否存在（仅未删除）
     */
    @Query("SELECT CASE WHEN COUNT(ag) > 0 THEN true ELSE false END FROM AuditGroup ag " +
           "WHERE ag.groupName = :groupName AND ag.deleted = false")
    boolean existsByGroupNameAndDeletedFalse(@Param("groupName") String groupName);

    /**
     * 检查组名是否存在 (排除指定ID)
     */
    @Query("SELECT CASE WHEN COUNT(ag) > 0 THEN true ELSE false END FROM AuditGroup ag " +
           "WHERE ag.groupName = :groupName AND ag.id != :id AND ag.deleted = false")
    boolean existsByGroupNameAndIdNot(@Param("groupName") String groupName, @Param("id") String id);

    /**
     * 检查组编码是否存在
     */
    boolean existsByGroupCode(String groupCode);

    /**
     * 检查组编码是否存在（仅未删除）
     */
    @Query("SELECT CASE WHEN COUNT(ag) > 0 THEN true ELSE false END FROM AuditGroup ag " +
           "WHERE ag.groupCode = :groupCode AND ag.deleted = false")
    boolean existsByGroupCodeAndDeletedFalse(@Param("groupCode") String groupCode);

    /**
     * 检查组编码是否存在 (排除指定ID)
     */
    @Query("SELECT CASE WHEN COUNT(ag) > 0 THEN true ELSE false END FROM AuditGroup ag " +
           "WHERE ag.groupCode = :groupCode AND ag.id != :id AND ag.deleted = false")
    boolean existsByGroupCodeAndIdNot(@Param("groupCode") String groupCode, @Param("id") String id);

    /**
     * 根据ID查询未删除的审核组
     */
    @Query("SELECT ag FROM AuditGroup ag WHERE ag.id = :id AND ag.deleted = false")
    Optional<AuditGroup> findByIdAndDeletedFalse(@Param("id") String id);
}
