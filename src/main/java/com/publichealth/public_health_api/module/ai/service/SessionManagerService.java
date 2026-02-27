package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.entity.AiSession;
import com.publichealth.public_health_api.module.ai.entity.ChatMessage;

import java.util.List;

/**
 * 会话管理服务接口
 */
public interface SessionManagerService {

    /**
     * 创建新会话
     *
     * @param request 创建会话请求
     * @return 会话 ID
     */
    String createSession(CreateSessionRequest request);

    /**
     * 获取会话详情
     *
     * @param sessionId 会话 ID
     * @return 会话详情
     */
    SessionDetailResponse getSessionDetail(String sessionId);

    /**
     * 添加消息到会话
     *
     * @param sessionId 会话 ID
     * @param role      角色 (user/assistant/system)
     * @param content   消息内容
     * @param metadata  元数据 JSON
     */
    void addMessage(String sessionId, String role, String content, String metadata);

    /**
     * 获取会话历史消息
     *
     * @param sessionId 会话 ID
     * @param limit     限制数量
     * @return 历史消息列表
     */
    List<ChatMessage> getSessionHistory(String sessionId, int limit);

    /**
     * 删除会话（软删除）
     *
     * @param sessionId 会话 ID
     */
    void deleteSession(String sessionId);

    /**
     * 获取或创建会话
     *
     * @param sessionId 会话 ID（为空时创建新会话）
     * @return 会话 ID
     */
    String getOrCreateSession(String sessionId);
}
