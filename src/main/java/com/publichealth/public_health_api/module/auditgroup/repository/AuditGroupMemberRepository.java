package com.publichealth.public_health_api.module.auditgroup.repository;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 审核组成员关联数据访问层
 */
@Repository
public interface AuditGroupMemberRepository extends JpaRepository<AuditGroupMember, String> {

    /**
     * 根据审核组ID查询所有成员
     */
    List<AuditGroupMember> findByGroupId(String groupId);

    /**
     * 根据用户ID查询所有所属组
     */
    List<AuditGroupMember> findByUserId(String userId);

    /**
     * 检查用户是否在指定组中
     */
    Optional<AuditGroupMember> findByGroupIdAndUserId(String groupId, String userId);

    /**
     * 查询用户所在的组ID列表
     */
    @Query("SELECT agm.groupId FROM AuditGroupMember agm WHERE agm.userId = :userId")
    List<String> findGroupIdsByUserId(@Param("userId") String userId);

    /**
     * 查询指定组的成员用户ID列表
     */
    @Query("SELECT agm.userId FROM AuditGroupMember agm WHERE agm.groupId = :groupId")
    List<String> findUserIdsByGroupId(@Param("groupId") String groupId);

    /**
     * 检查用户是否在指定组中
     */
    @Query("SELECT CASE WHEN COUNT(agm) > 0 THEN true ELSE false END FROM AuditGroupMember agm " +
           "WHERE agm.groupId = :groupId AND agm.userId = :userId")
    boolean existsByGroupIdAndUserId(@Param("groupId") String groupId, @Param("userId") String userId);

    /**
     * 统计指定组的成员数量
     */
    @Query("SELECT COUNT(agm) FROM AuditGroupMember agm WHERE agm.groupId = :groupId")
    long countByGroupId(@Param("groupId") String groupId);

    /**
     * 统计用户所属的组数量
     */
    @Query("SELECT COUNT(agm) FROM AuditGroupMember agm WHERE agm.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    /**
     * 删除指定组的所有成员
     */
    void deleteByGroupId(String groupId);

    /**
     * 删除用户在所有组的成员关系
     */
    void deleteByUserId(String userId);

    /**
     * 删除指定组的指定成员
     */
    void deleteByGroupIdAndUserId(String groupId, String userId);
}
