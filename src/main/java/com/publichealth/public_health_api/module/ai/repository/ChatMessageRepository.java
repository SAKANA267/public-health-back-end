package com.publichealth.public_health_api.module.ai.repository;

import com.publichealth.public_health_api.module.ai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息数据访问层
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * 根据会话 ID 查询消息列表，按创建时间排序
     */
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * 根据会话 ID 查询最近 N 条消息
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.sessionId = :sessionId ORDER BY m.createdAt DESC")
    List<ChatMessage> findRecentMessages(@Param("sessionId") String sessionId);
}
