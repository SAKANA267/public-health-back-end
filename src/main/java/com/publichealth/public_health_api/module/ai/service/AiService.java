package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.response.ChatResponse;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;

/**
 * AI 服务接口
 * 组合各个子服务，提供完整的 AI 助手功能
 */
public interface AiService {

    /**
     * 聊天对话主流程
     * 包含意图识别、意图执行、历史保存
     *
     * @param request 聊天请求
     * @param userId  用户 ID
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request, String userId);

    /**
     * 识别用户意图
     *
     * @param message 用户消息
     * @return 意图识别结果
     */
    IntentResponse recognizeIntent(String message);

    /**
     * 执行意图
     *
     * @param request 执行请求
     * @return 聊天响应
     */
    ChatResponse execute(ExecuteRequest request);

    /**
     * 创建会话
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
     * 删除会话
     *
     * @param sessionId 会话 ID
     */
    void deleteSession(String sessionId);
}
