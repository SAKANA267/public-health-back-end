package com.publichealth.public_health_api.module.auth.repository;

import com.publichealth.public_health_api.module.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 刷新令牌数据访问层
 * 继承 JpaRepository 获得基础 CRUD 功能
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    // ============================================
    // 基础查询方法 (Spring Data JPA 自动实现)
    // ============================================

    /**
     * 根据令牌字符串查询
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 根据令牌字符串查询未删除的记录
     */
    Optional<RefreshToken> findByTokenAndDeletedFalse(String token);

    /**
     * 根据用户ID查询所有有效的刷新令牌
     */
    List<RefreshToken> findByUserIdAndDeletedFalse(String userId);

    // ============================================
    // 逻辑删除相关查询
    // ============================================

    /**
     * 查询所有未删除的记录
     */
    List<RefreshToken> findByDeletedFalse();

    // ============================================
    // 自定义 JPQL 查询
    // ============================================

    /**
     * 查询用户的有效刷新令牌（未过期且未删除）
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.deleted = false " +
           "AND rt.expiryTime > :now")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * 删除用户的所有刷新令牌（逻辑删除）
     */
    @Query("UPDATE RefreshToken rt SET rt.deleted = true WHERE rt.userId = :userId")
    void revokeAllUserTokens(@Param("userId") String userId);

    /**
     * 删除指定的刷新令牌（逻辑删除）
     */
    @Query("UPDATE RefreshToken rt SET rt.deleted = true WHERE rt.token = :token")
    void revokeToken(@Param("token") String token);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查令牌是否存在
     */
    boolean existsByToken(String token);
}
