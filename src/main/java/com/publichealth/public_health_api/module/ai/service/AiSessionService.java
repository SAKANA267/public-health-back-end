package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequestDTO;
import com.publichealth.public_health_api.module.ai.dto.response.MessageDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;

import java.util.List;

/**
 * AI会话服务接口
 * 负责会话和消息的数据库持久化操作
 */
public interface AiSessionService {

    /**
     * 创建新会话
     *
     * @param userId 用户ID
     * @param title  会话标题（可选）
     * @return 会话DTO
     */
    SessionDTO createSession(String userId, String title);

    /**
     * 创建新会话（使用请求DTO）
     *
     * @param request 创建会话请求
     * @param userId  用户ID（从JWT获取）
     * @return 会话DTO
     */
    SessionDTO createSession(CreateSessionRequestDTO request, String userId);

    /**
     * 获取用户的所有会话列表
     *
     * @param userId 用户ID
     * @return 会话DTO列表
     */
    List<SessionDTO> getUserSessions(String userId);

    /**
     * 获取会话详情（包含历史消息）
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     * @return 会话详情响应
     */
    SessionDetailResponse getSessionDetail(String sessionId, String userId);

    /**
     * 获取会话的历史消息列表
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     * @return 消息DTO列表
     */
    List<MessageDTO> getSessionMessages(String sessionId, String userId);

    /**
     * 保存用户消息到数据库
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @param content   消息内容
     * @return 消息DTO
     */
    MessageDTO saveUserMessage(String sessionId, String userId, String content);

    /**
     * 保存AI助手消息到数据库
     *
     * @param sessionId   会话ID
     * @param userId      用户ID
     * @param content     消息内容
     * @param messageType 消息类型
     * @param metadata    元数据（JSON字符串）
     * @return 消息DTO
     */
    MessageDTO saveAssistantMessage(String sessionId, String userId, String content, String messageType, String metadata);

    /**
     * 更新会话标题
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     * @param title     新标题
     */
    void updateSessionTitle(String sessionId, String userId, String title);

    /**
     * 删除会话（软删除）
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     */
    void deleteSession(String sessionId, String userId);

    /**
     * 检查用户是否拥有指定会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 是否拥有
     */
    boolean isSessionOwnedByUser(String sessionId, String userId);

    /**
     * 更新会话的最后消息时间
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     */
    void updateLastMessageTime(String sessionId, String userId);

    /**
     * 加载会话的前N条消息到Spring AI的ChatMemory
     * 用于在重启应用后恢复会话上下文
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @param limit     消息数量限制
     * @return 消息内容列表（按时间顺序）
     */
    List<String> loadRecentMessagesForContext(String sessionId, String userId, int limit);
}
