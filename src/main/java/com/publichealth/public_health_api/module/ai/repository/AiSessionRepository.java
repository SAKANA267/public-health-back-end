package com.publichealth.public_health_api.module.ai.repository;

import com.publichealth.public_health_api.module.ai.entity.AiSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 会话数据访问层
 */
@Repository
public interface AiSessionRepository extends JpaRepository<AiSession, String> {

    /**
     * 根据用户 ID 查询未删除的会话列表
     */
    List<AiSession> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(String userId);

    /**
     * 根据用户 ID 和会话 ID 查询
     */
    Optional<AiSession> findByIdAndDeletedFalse(String id);

    /**
     * 根据用户 ID 统计未删除的会话数量
     */
    long countByUserIdAndDeletedFalse(String userId);

    /**
     * 软删除会话
     */
    @org.springframework.transaction.annotation.Transactional
    default void softDelete(String sessionId) {
        findById(sessionId).ifPresent(session -> {
            session.setDeleted(true);
            save(session);
        });
    }
}
