package com.publichealth.public_health_api.module.ai.client;

import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.enums.IntentType;

import java.util.List;

/**
 * LLM 客户端接口
 * 定义与大语言模型交互的方法
 */
public interface LlmClient {

    /**
     * 识别用户意图
     *
     * @param userMessage 用户输入消息
     * @param history     历史对话上下文
     * @return 意图识别结果
     */
    IntentResponse recognizeIntent(String userMessage, List<ChatContext> history);

    /**
     * 生成聊天回复
     *
     * @param userMessage 用户输入消息
     * @param intent      识别出的意图
     * @return AI 回复内容
     */
    String generateChatReply(String userMessage, IntentResponse intent);

    /**
     * 生成建议操作
     *
     * @param intent 意图类型
     * @param entity 实体类型
     * @return 建议操作列表
     */
    List<String> generateSuggestions(IntentType intent, String entity);

    /**
     * 聊天上下文记录
     */
    record ChatContext(String role, String content) {
    }
}
