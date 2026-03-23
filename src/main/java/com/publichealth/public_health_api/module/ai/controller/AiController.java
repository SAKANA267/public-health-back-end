package com.publichealth.public_health_api.module.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.context.UserContext;
import com.publichealth.public_health_api.module.ai.dto.AiResponse;
import com.publichealth.public_health_api.module.ai.dto.IntentResult;
import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequestDTO;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.request.IntentRequest;
import com.publichealth.public_health_api.module.ai.dto.response.MessageDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionResponse;
import com.publichealth.public_health_api.module.ai.service.AiSessionService;
import com.publichealth.public_health_api.module.ai.service.SpringAiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 助手 REST 控制器 - Spring AI 版本
 * 支持同步和流式响应
 * 支持会话历史数据库持久化
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final SpringAiChatService aiService;
    private final AiSessionService aiSessionService;
    private final ObjectMapper objectMapper;

    /**
     * 聊天对话接口（同步）
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(@Valid @RequestBody ChatRequest request) {
        String userId = UserContext.getUserId();
        log.info("收到聊天请求: userId={}, sessionId={}, message={}", userId, request.getSessionId(),
                request.getMessage());

        // 如果没有sessionId，创建新会话
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            SessionDTO newSession = aiSessionService.createSession(userId, "新对话");
            request.setSessionId(newSession.getSessionId());
            log.info("创建新会话: sessionId={}", newSession.getSessionId());
        }

        // 保存用户消息到数据库
        aiSessionService.saveUserMessage(request.getSessionId(), userId, request.getMessage());

        // 调用AI服务
        AiResponse response = aiService.chat(request, userId);

        // 保存AI响应到数据库
        String metadata = null;
        if (response.action() != null) {
            metadata = convertActionToJson(response.action());
        }
        aiSessionService.saveAssistantMessage(request.getSessionId(), userId, response.message(),
                "action", metadata);

        // 更新会话标题（如果是首条消息）
        String title = generateTitleFromMessage(request.getMessage());
        aiSessionService.updateSessionTitle(request.getSessionId(), userId, title);

        return ApiResponse.success(response);
    }

    /**
     * 聊天对话接口（流式）
     *
     * 返回 Server-Sent Events (SSE) 流，实时输出 AI 响应
     *
     * @param request 聊天请求
     * @return 流式响应
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody ChatRequest request) {
        String userId = UserContext.getUserId();
        log.info("收到流式聊天请求: userId={}, sessionId={}, message={}", userId, request.getSessionId(),
                request.getMessage());

        // 如果没有sessionId，创建新会话
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            SessionDTO newSession = aiSessionService.createSession(userId, "新对话");
            request.setSessionId(newSession.getSessionId());
            log.info("创建新会话: sessionId={}", newSession.getSessionId());
        }

        // 保存用户消息到数据库
        aiSessionService.saveUserMessage(request.getSessionId(), userId, request.getMessage());

        // 更新会话标题（如果是首条消息）
        String title = generateTitleFromMessage(request.getMessage());
        aiSessionService.updateSessionTitle(request.getSessionId(), userId, title);

        // 返回流式响应
        return aiService.chatStream(request, userId)
                .doOnComplete(() -> {
                    // 流式完成后更新会话时间
                    aiSessionService.updateLastMessageTime(request.getSessionId(), userId);
                })
                .doOnError(e -> {
                    log.error("流式聊天失败: {}", e.getMessage());
                });
    }

    /**
     * 意图识别接口
     *
     * @param request 意图识别请求
     * @return 意图识别结果
     */
    @PostMapping("/intent")
    public ApiResponse<IntentResult> recognizeIntent(@Valid @RequestBody IntentRequest request) {
        log.info("收到意图识别请求: message={}", request.getMessage());
        IntentResult response = aiService.recognizeIntent(request.getMessage());
        return ApiResponse.success(response);
    }

    /**
     * 意图执行接口
     *
     * @param request 意图执行请求
     * @return 聊天响应
     */
    @PostMapping("/execute")
    public ApiResponse<AiResponse> execute(@Valid @RequestBody ExecuteRequest request) {
        log.info("收到意图执行请求: intent={}, entity={}", request.getIntent(), request.getEntity());
        AiResponse response = aiService.executeIntent(request);
        return ApiResponse.success(response);
    }

    /**
     * 创建会话（新版，使用数据库持久化）
     *
     * @param request 创建会话请求
     * @return 会话DTO
     */
    @PostMapping("/sessions")
    public ApiResponse<SessionDTO> createSession(@RequestBody CreateSessionRequestDTO request) {
        String userId = UserContext.getUserId();
        log.info("收到创建会话请求: userId={}, title={}", userId, request.getTitle());
        SessionDTO response = aiSessionService.createSession(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 获取用户的所有会话列表
     *
     * @return 会话DTO列表
     */
    @GetMapping("/sessions")
    public ApiResponse<List<SessionDTO>> getUserSessions() {
        String userId = UserContext.getUserId();
        log.info("获取用户会话列表: userId={}", userId);
        List<SessionDTO> sessions = aiSessionService.getUserSessions(userId);
        return ApiResponse.success(sessions);
    }

    /**
     * 获取会话详情（包含历史消息）
     *
     * @param sessionId 会话 ID
     * @return 会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<SessionDetailResponse> getSession(@PathVariable String sessionId) {
        String userId = UserContext.getUserId();
        log.info("收到获取会话请求: sessionId={}, userId={}", sessionId, userId);
        SessionDetailResponse response = aiSessionService.getSessionDetail(sessionId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 获取会话的消息列表
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<List<MessageDTO>> getSessionMessages(@PathVariable String sessionId) {
        String userId = UserContext.getUserId();
        log.info("收到获取会话消息请求: sessionId={}, userId={}", sessionId, userId);
        List<MessageDTO> messages = aiSessionService.getSessionMessages(sessionId, userId);
        return ApiResponse.success(messages);
    }

    /**
     * 删除会话（软删除）
     *
     * @param sessionId 会话 ID
     * @return 成功响应
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable String sessionId) {
        String userId = UserContext.getUserId();
        log.info("收到删除会话请求: sessionId={}, userId={}", sessionId, userId);
        aiSessionService.deleteSession(sessionId, userId);
        return ApiResponse.success("会话已删除");
    }

    /**
     * 生成会话标题（从首条消息）
     *
     * @param message 消息内容
     * @return 标题
     */
    private String generateTitleFromMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "新对话";
        }
        // 取前20个字符作为标题
        return message.length() > 20 ? message.substring(0, 20) + "..." : message;
    }

    /**
     * 将 Action 对象转换为 JSON 字符串
     *
     * @param action Action 对象
     * @return JSON 字符串
     */
    private String convertActionToJson(AiResponse.Action action) {
        if (action == null) {
            return null;
        }
        try {
            // 创建一个简单的Map来表示action
            java.util.Map<String, Object> actionMap = new java.util.HashMap<>();
            actionMap.put("type", action.type());
            actionMap.put("payload", action.payload().data());
            return objectMapper.writeValueAsString(actionMap);
        } catch (Exception e) {
            log.error("Action转JSON失败", e);
            return null;
        }
    }
}
