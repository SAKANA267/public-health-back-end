package com.publichealth.public_health_api.module.ai.repository;

import com.publichealth.public_health_api.module.ai.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI聊天消息 Repository 接口
 */
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, String> {

    /**
     * 查询会话的所有消息（按创建时间升序排列）
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * 统计会话的消息数量
     *
     * @param sessionId 会话ID
     * @return 消息数量
     */
    long countBySessionId(String sessionId);

    /**
     * 删除会话的所有消息（物理删除）
     *
     * @param sessionId 会话ID
     */
    @Transactional
    void deleteBySessionId(String sessionId);

    /**
     * 查询会话的前N条消息（用于加载上下文）
     * 使用原生SQL查询支持LIMIT
     *
     * @param sessionId 会话ID
     * @param limit     限制数量
     * @return 消息列表
     */
    @Query(value = "SELECT * FROM ai_chat_message WHERE session_id = :sessionId ORDER BY created_at ASC LIMIT :limit", nativeQuery = true)
    List<AiChatMessage> findRecentMessagesBySessionId(@Param("sessionId") String sessionId, @Param("limit") int limit);

    /**
     * 查询会话的前N条消息（用于加载上下文）
     * 使用JPQL查询支持Pageable
     *
     * @param sessionId 会话ID
     * @return 消息列表（最多返回指定数量）
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.sessionId = :sessionId ORDER BY m.createdAt ASC")
    List<AiChatMessage> findAllBySessionIdOrderByCreatedAtAsc(@Param("sessionId") String sessionId);
}
