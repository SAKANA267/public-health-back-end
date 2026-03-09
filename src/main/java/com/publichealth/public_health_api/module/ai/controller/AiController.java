package com.publichealth.public_health_api.module.ai.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.context.UserContext;
import com.publichealth.public_health_api.module.ai.dto.AiResponse;
import com.publichealth.public_health_api.module.ai.dto.IntentResult;
import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.request.IntentRequest;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionResponse;
import com.publichealth.public_health_api.module.ai.service.SpringAiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI 助手 REST 控制器 - Spring AI 版本
 * 支持同步和流式响应
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final SpringAiChatService aiService;

    /**
     * 聊天对话接口（同步）
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(@Valid @RequestBody ChatRequest request) {
        String userId = UserContext.getUserId();
        log.info("收到聊天请求: userId={}, message={}", userId, request.getMessage());
        AiResponse response = aiService.chat(request, userId);
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
        log.info("收到流式聊天请求: userId={}, message={}", userId, request.getMessage());
        return aiService.chatStream(request, userId);
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
     * 创建会话
     *
     * @param request 创建会话请求
     * @return 会话响应
     */
    @PostMapping("/sessions")
    public ApiResponse<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        log.info("收到创建会话请求: userId={}", request.getUserId());
        SessionResponse response = aiService.createSession(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取会话历史
     *
     * @param sessionId 会话 ID
     * @return 会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<SessionDetailResponse> getSession(@PathVariable String sessionId) {
        log.info("收到获取会话请求: sessionId={}", sessionId);
        SessionDetailResponse response = aiService.getSessionDetail(sessionId);
        return ApiResponse.success(response);
    }

    /**
     * 删除会话
     *
     * @param sessionId 会话 ID
     * @return 成功响应
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable String sessionId) {
        log.info("收到删除会话请求: sessionId={}", sessionId);
        aiService.deleteSession(sessionId);
        return ApiResponse.success("会话已删除");
    }
}
