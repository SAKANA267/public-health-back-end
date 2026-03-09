package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.dto.AiResponse;
import com.publichealth.public_health_api.module.ai.dto.IntentResult;
import com.publichealth.public_health_api.module.ai.dto.StructuredChatResponse;
import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionResponse;
import com.publichealth.public_health_api.module.ai.exception.AiProcessingException;
import com.publichealth.public_health_api.module.ai.exception.AiServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring AI 聊天服务
 * 使用注入的 ChatClient Bean，带错误处理、重试机制和流式响应
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiChatService {

    private final ChatClient chatClient;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final ChatClient intentRecognitionClient;
    private final ChatMemory chatMemory;
    private final Map<String, SessionMetadata> sessionMetadata = new ConcurrentHashMap<>();

    @Retryable(retryFor = {AiServiceUnavailableException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public AiResponse chat(ChatRequest request, String userId) {
        log.info("[Spring AI] 聊天请求: userId={}, message={}", userId, request.getMessage());
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = createSessionInternal(userId);
        }
        final String conversationId = sessionId;
        long startTime = System.currentTimeMillis();
        try {
            StructuredChatResponse structuredResponse = chatClient.prompt()
                    .user(request.getMessage())
                    .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
                    .call()
                    .entity(StructuredChatResponse.class);
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Spring AI] LLM 响应完成: {}ms", duration);
            log.info("[Spring AI] AI返回值: message={}, action={}", structuredResponse.message(), structuredResponse.action());

            // 转换为AiResponse
            AiResponse.Action action = null;
            if (structuredResponse.action() != null && structuredResponse.action().type() != null) {
                action = new AiResponse.Action(
                        structuredResponse.action().type(),
                        new AiResponse.MapWrapper(structuredResponse.action().payload())
                );
            }

            List<String> suggestions = generateSuggestions(structuredResponse.message());
            updateSessionMetadata(sessionId, request.getMessage());
            return new AiResponse(structuredResponse.message(), action, suggestions, sessionId);
        } catch (RuntimeException e) {
            if (isNetworkRelatedException(e)) {
                log.error("AI 模型调用失败: {}", e.getMessage(), e);
                throw new AiServiceUnavailableException("AI 服务暂时不可用", e);
            }
            log.error("AI 调用处理失败: {}", e.getMessage(), e);
            throw new AiProcessingException("处理请求时发生错误", e);
        }
    }

    public Flux<String> chatStream(ChatRequest request, String userId) {
        log.info("[Spring AI] 流式聊天请求: userId={}, message={}", userId, request.getMessage());
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = createSessionInternal(userId);
        }
        final String conversationId = sessionId;
        final String finalSessionId = sessionId;
        final String firstMessage = request.getMessage();
        final long startTime = System.currentTimeMillis();
        return chatClient.prompt()
                .user(request.getMessage())
                .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
                .stream()
                .content()
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[Spring AI] 流式响应完成: {}ms", duration);
                    updateSessionMetadata(finalSessionId, firstMessage);
                })
                .doOnError(e -> log.error("[Spring AI] 流式响应失败: {}", e.getMessage(), e));
    }

    @Retryable(retryFor = {AiServiceUnavailableException.class}, maxAttempts = 2, backoff = @Backoff(delay = 500))
    public IntentResult recognizeIntent(String userMessage) {
        log.info("[Spring AI] 意图识别请求: message={}", userMessage);
        try {
            IntentResult result = intentRecognitionClient.prompt()
                    .user(userMessage)
                    .call()
                    .entity(IntentResult.class);
            log.info("[Spring AI] 意图识别完成: intent={}, entity={}, params={}, confidence={}",
                    result.intent(), result.entity(), result.params(), result.confidence());
            return result;
        } catch (RuntimeException e) {
            if (isNetworkRelatedException(e)) {
                log.error("意图识别失败: {}", e.getMessage(), e);
                throw new AiServiceUnavailableException("AI 服务暂时不可用", e);
            }
            log.error("意图识别处理失败: {}", e.getMessage(), e);
            throw new AiProcessingException("识别意图时发生错误", e);
        }
    }

    @Retryable(retryFor = {AiServiceUnavailableException.class}, maxAttempts = 2, backoff = @Backoff(delay = 500))
    public AiResponse executeIntent(ExecuteRequest request) {
        log.info("[Spring AI] 执行意图: intent={}, entity={}", request.getIntent(), request.getEntity());
        try {
            String naturalQuery = buildNaturalQuery(request);
            String response = chatClient.prompt()
                    .user(naturalQuery)
                    .call()
                    .content();
            log.info("[Spring AI] 意图执行完成, AI返回值: {}", response);
            return new AiResponse(response, null, generateSuggestions(response), request.getSessionId());
        } catch (RuntimeException e) {
            if (isNetworkRelatedException(e)) {
                log.error("意图执行失败: {}", e.getMessage(), e);
                throw new AiServiceUnavailableException("AI 服务暂时不可用", e);
            }
            log.error("意图执行处理失败: {}", e.getMessage(), e);
            throw new AiProcessingException("执行意图时发生错误", e);
        }
    }

    public SessionResponse createSession(CreateSessionRequest request) {
        String sessionId = createSessionInternal(request.getUserId());
        log.info("[Spring AI] 创建会话: sessionId={}", sessionId);
        return new SessionResponse(sessionId, System.currentTimeMillis());
    }

    public SessionDetailResponse getSessionDetail(String sessionId) {
        log.info("[Spring AI] 获取会话详情: sessionId={}", sessionId);
        SessionMetadata metadata = sessionMetadata.get(sessionId);
        if (metadata == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        List<Message> history = chatMemory.get(sessionId);
        List<SessionDetailResponse.MessageInfo> messages = history.stream()
                .map(msg -> SessionDetailResponse.MessageInfo.builder()
                        .role(msg instanceof org.springframework.ai.chat.messages.UserMessage ? "user" : "assistant")
                        .content(msg.getText())
                        .timestamp(System.currentTimeMillis())
                        .build())
                .toList();
        return SessionDetailResponse.builder()
                .sessionId(sessionId)
                .title(metadata.title())
                .messages(messages)
                .build();
    }

    public void deleteSession(String sessionId) {
        log.info("[Spring AI] 删除会话: sessionId={}", sessionId);
        chatMemory.clear(sessionId);
        sessionMetadata.remove(sessionId);
    }

    private boolean isNetworkRelatedException(Throwable e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        return message.contains("timeout") || message.contains("connection") || message.contains("network") ||
                message.contains("503") || message.contains("502") ||
                (e.getCause() != null && isNetworkRelatedException(e.getCause()));
    }

    private String createSessionInternal(String userId) {
        String sessionId = UUID.randomUUID().toString();
        sessionMetadata.put(sessionId, new SessionMetadata(sessionId, userId, "新对话", System.currentTimeMillis()));
        return sessionId;
    }

    private void updateSessionMetadata(String sessionId, String firstMessage) {
        SessionMetadata metadata = sessionMetadata.get(sessionId);
        if (metadata != null && "新对话".equals(metadata.title())) {
            String title = firstMessage.length() > 20 ? firstMessage.substring(0, 20) + "..." : firstMessage;
            sessionMetadata.put(sessionId, new SessionMetadata(
                    metadata.sessionId(), metadata.userId(), title, metadata.createdAt()));
        }
    }

    private String buildNaturalQuery(ExecuteRequest request) {
        StringBuilder sb = new StringBuilder();
        switch (request.getIntent()) {
            case "navigate":
                sb.append("导航到").append(request.getEntity()).append("页面");
                break;
            case "create":
                sb.append("创建").append(request.getEntity());
                break;
            case "read":
            case "query":
                sb.append("查询").append(request.getEntity());
                break;
            case "update":
                sb.append("更新").append(request.getEntity());
                break;
            case "delete":
                sb.append("删除").append(request.getEntity());
                break;
            case "count":
                sb.append("统计").append(request.getEntity()).append("的数量");
                break;
            default:
                sb.append("处理").append(request.getEntity());
        }
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            sb.append("，参数：").append(request.getParams());
        }
        return sb.toString();
    }

    private List<String> generateSuggestions(String response) {
        if (response.contains("导航") || response.contains("跳转")) {
            return List.of("查看详情", "返回");
        } else if (response.contains("创建") || response.contains("添加")) {
            return List.of("继续添加", "查看列表", "返回");
        } else if (response.contains("查询") || response.contains("查看")) {
            return List.of("筛选数据", "导出报表", "新增数据");
        } else if (response.contains("删除")) {
            return List.of("确认删除", "取消", "返回列表");
        } else if (response.contains("统计")) {
            return List.of("查看列表", "导出报表");
        }
        return List.of("查看帮助", "返回首页");
    }

    private record SessionMetadata(String sessionId, String userId, String title, Long createdAt) {}
}
