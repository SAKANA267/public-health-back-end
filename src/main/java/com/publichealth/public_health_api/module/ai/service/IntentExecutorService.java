package com.publichealth.public_health_api.module.ai.service;

import com.publichealth.public_health_api.module.ai.dto.response.ChatResponse;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;

/**
 * 意图执行服务接口
 */
public interface IntentExecutorService {

    /**
     * 执行识别出的意图
     *
     * @param intent 意图识别结果
     * @return 聊天响应
     */
    ChatResponse execute(IntentResponse intent);

    /**
     * 根据原始请求执行意图
     *
     * @param userMessage 用户输入消息
     * @param intent      意图识别结果
     * @return 聊天响应
     */
    ChatResponse execute(String userMessage, IntentResponse intent);
}
