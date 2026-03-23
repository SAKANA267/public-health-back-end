package com.publichealth.public_health_api.module.ai.repository;

import com.publichealth.public_health_api.module.ai.entity.AiSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * AI会话 Repository 接口
 */
public interface AiSessionRepository extends JpaRepository<AiSession, String> {

    /**
     * 查询用户的所有会话（排除已删除）
     * 按创建时间倒序排列
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiSession> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(String userId);

    /**
     * 查询用户的所有会话（排除已删除）
     * 按最后消息时间倒序排列
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiSession> findByUserIdAndDeletedFalseOrderByLastMessageAtDesc(String userId);

    /**
     * 查询用户的会话总数（排除已删除）
     *
     * @param userId 用户ID
     * @return 会话总数
     */
    long countByUserIdAndDeletedFalse(String userId);

    /**
     * 根据ID和用户ID查询会话（确保数据隔离）
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 会话对象
     */
    Optional<AiSession> findByIdAndUserIdAndDeletedFalse(String sessionId, String userId);

    /**
     * 检查用户是否拥有指定会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 是否拥有
     */
    boolean existsByIdAndUserIdAndDeletedFalse(String sessionId, String userId);
}
