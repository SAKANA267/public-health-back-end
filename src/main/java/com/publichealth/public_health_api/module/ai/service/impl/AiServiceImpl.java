package com.publichealth.public_health_api.module.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.module.ai.client.LlmClient;
import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.response.ChatResponse;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.enums.IntentType;
import com.publichealth.public_health_api.module.ai.service.AiService;
import com.publichealth.public_health_api.module.ai.service.IntentExecutorService;
import com.publichealth.public_health_api.module.ai.service.IntentRecognizerService;
import com.publichealth.public_health_api.module.ai.service.SessionManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 服务实现
 * 组合各个子服务，提供完整的 AI 助手功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final IntentRecognizerService intentRecognizerService;
    private final IntentExecutorService intentExecutorService;
    private final SessionManagerService sessionManagerService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ChatResponse chat(ChatRequest request, String userId) {
        log.info("处理聊天请求: sessionId={}, userId={}, message={}",
                request.getSessionId(), userId, request.getMessage());

        // 1. 获取或创建会话
        String sessionId = sessionManagerService.getOrCreateSession(request.getSessionId());

        // 2. 保存用户消息
        sessionManagerService.addMessage(sessionId, "user", request.getMessage(), null);

        // 3. 构建历史上下文
        List<LlmClient.ChatContext> history = buildChatContext(request);

        // 4. 识别意图
        IntentResponse intent = intentRecognizerService.recognize(request.getMessage(), history);

        // 5. 执行意图
        ChatResponse response = intentExecutorService.execute(request.getMessage(), intent);

        // 设置会话ID到响应中
        response.setSessionId(sessionId);

        // 6. 保存 AI 回复
        try {
            String metadata = objectMapper.writeValueAsString(response);
            sessionManagerService.addMessage(sessionId, "assistant", response.getMessage(), metadata);
        } catch (JsonProcessingException e) {
            log.warn("序列化响应元数据失败", e);
            sessionManagerService.addMessage(sessionId, "assistant", response.getMessage(), null);
        }

        // 7. 更新会话标题（首条消息）
        updateSessionTitleIfNeeded(sessionId, request.getMessage());

        log.info("聊天处理完成: sessionId={}, intent={}", sessionId, intent.getIntent());
        return response;
    }

    @Override
    public IntentResponse recognizeIntent(String message) {
        return intentRecognizerService.recognize(message);
    }

    @Override
    public ChatResponse execute(ExecuteRequest request) {
        IntentResponse intent = IntentResponse.builder()
                .intent(IntentType.fromCode(request.getIntent()))
                .params(request.getParams())
                .build();

        return intentExecutorService.execute(intent);
    }

    @Override
    public String createSession(CreateSessionRequest request) {
        return sessionManagerService.createSession(request);
    }

    @Override
    public SessionDetailResponse getSessionDetail(String sessionId) {
        return sessionManagerService.getSessionDetail(sessionId);
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionManagerService.deleteSession(sessionId);
    }

    /**
     * 构建聊天上下文
     */
    private List<LlmClient.ChatContext> buildChatContext(ChatRequest request) {
        if (request.getContext() != null && request.getContext().getPreviousMessages() != null) {
            return request.getContext().getPreviousMessages().stream()
                    .map(msg -> new LlmClient.ChatContext(msg.getRole(), msg.getContent()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * 如果是首条消息，更新会话标题
     */
    private void updateSessionTitleIfNeeded(String sessionId, String firstMessage) {
        // 这里可以根据需要实现标题更新逻辑
        // 例如：截取消息的前 20 个字符作为标题
        log.debug("首条消息，可更新会话标题: sessionId={}, message={}", sessionId, firstMessage);
    }
}
