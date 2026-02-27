package com.publichealth.public_health_api.module.ai.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.context.UserContext;
import com.publichealth.public_health_api.module.ai.dto.request.ChatRequest;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.request.ExecuteRequest;
import com.publichealth.public_health_api.module.ai.dto.request.IntentRequest;
import com.publichealth.public_health_api.module.ai.dto.response.ChatResponse;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.dto.response.SessionResponse;
import com.publichealth.public_health_api.module.ai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 助手 REST 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 聊天对话接口
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String userId = UserContext.getUserId();
        log.info("收到聊天请求: userId={}, message={}", userId, request.getMessage());
        ChatResponse response = aiService.chat(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 意图识别接口
     *
     * @param request 意图识别请求
     * @return 意图识别结果
     */
    @PostMapping("/intent")
    public ApiResponse<IntentResponse> recognizeIntent(@Valid @RequestBody IntentRequest request) {
        log.info("收到意图识别请求: message={}", request.getMessage());
        IntentResponse response = aiService.recognizeIntent(request.getMessage());
        return ApiResponse.success(response);
    }

    /**
     * 意图执行接口
     *
     * @param request 意图执行请求
     * @return 聊天响应
     */
    @PostMapping("/execute")
    public ApiResponse<ChatResponse> execute(@Valid @RequestBody ExecuteRequest request) {
        log.info("收到意图执行请求: intent={}, entity={}", request.getIntent(), request.getEntity());
        ChatResponse response = aiService.execute(request);
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
        String sessionId = aiService.createSession(request);
        return ApiResponse.success(new SessionResponse(sessionId, System.currentTimeMillis()));
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
