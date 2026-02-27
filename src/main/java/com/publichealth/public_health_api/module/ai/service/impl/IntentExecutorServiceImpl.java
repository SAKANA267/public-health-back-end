package com.publichealth.public_health_api.module.ai.service.impl;

import com.publichealth.public_health_api.module.ai.client.LlmClient;
import com.publichealth.public_health_api.module.ai.dto.response.Action;
import com.publichealth.public_health_api.module.ai.dto.response.ChatResponse;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.enums.ActionType;
import com.publichealth.public_health_api.module.ai.enums.EntityType;
import com.publichealth.public_health_api.module.ai.enums.IntentType;
import com.publichealth.public_health_api.module.ai.service.IntentExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意图执行服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentExecutorServiceImpl implements IntentExecutorService {

    private final LlmClient llmClient;

    @Override
    public ChatResponse execute(IntentResponse intent) {
        return execute(null, intent);
    }

    @Override
    public ChatResponse execute(String userMessage, IntentResponse intent) {
        log.info("执行意图: intent={}, entity={}", intent.getIntent(), intent.getEntity());

        // 生成回复消息
        String message = llmClient.generateChatReply(userMessage, intent);

        // 生成操作指令
        Action action = buildAction(intent);

        // 生成建议操作
        List<String> suggestions = llmClient.generateSuggestions(
                intent.getIntent(),
                intent.getEntity() != null ? intent.getEntity().getCode() : null
        );

        return ChatResponse.builder()
                .message(message)
                .action(action)
                .suggestions(suggestions)
                .build();
    }

    /**
     * 根据意图构建操作指令
     */
    private Action buildAction(IntentResponse intent) {
        if (intent.getIntent() == IntentType.UNKNOWN) {
            return null;
        }

        ActionType actionType = determineActionType(intent);
        Map<String, Object> payload = buildPayload(intent);

        return Action.builder()
                .type(actionType)
                .payload(payload)
                .build();
    }

    /**
     * 确定操作类型
     */
    private ActionType determineActionType(IntentResponse intent) {
        return switch (intent.getIntent()) {
            case NAVIGATE -> ActionType.NAVIGATE;
            case CREATE, UPDATE, DELETE, QUERY, COUNT -> ActionType.API;
            case HELP -> ActionType.CALLBACK;
            default -> null;
        };
    }

    /**
     * 构建操作参数
     */
    private Map<String, Object> buildPayload(IntentResponse intent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("intent", intent.getIntent().getCode());

        if (intent.getEntity() != null) {
            payload.put("entity", intent.getEntity().getCode());
        }

        if (intent.getParams() != null && !intent.getParams().isEmpty()) {
            payload.putAll(intent.getParams());
        }

        // 为导航意图添加路由信息
        if (intent.getIntent() == IntentType.NAVIGATE && intent.getEntity() != null) {
            payload.put("route", getRouteForEntity(intent.getEntity()));
        }

        return payload;
    }

    /**
     * 根据实体类型获取前端路由
     */
    private String getRouteForEntity(EntityType entity) {
        return switch (entity) {
            case REPORT_CARD -> "/report-cards";
            case USER -> "/users";
            case OBJECT -> "/objects";
            case AUDIT -> "/audit";
        };
    }
}
