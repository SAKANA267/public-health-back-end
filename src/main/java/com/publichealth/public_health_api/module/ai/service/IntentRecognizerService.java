package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.client.LlmClient;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;

import java.util.List;

/**
 * 意图识别服务接口
 */
public interface IntentRecognizerService {

    /**
     * 识别用户输入的意图
     *
     * @param userMessage 用户输入消息
     * @return 意图识别结果
     */
    IntentResponse recognize(String userMessage);

    /**
     * 识别用户输入的意图（带历史上下文）
     *
     * @param userMessage 用户输入消息
     * @param history     历史对话上下文
     * @return 意图识别结果
     */
    IntentResponse recognize(String userMessage, List<LlmClient.ChatContext> history);
}
