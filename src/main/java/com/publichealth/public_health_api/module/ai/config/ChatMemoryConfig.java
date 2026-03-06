package com.publichealth.public_health_api.module.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatMemory 配置类
 * 配置会话记忆存储
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * 会话记忆 Bean
     * 使用 MessageWindowChatMemory，保持最近的消息窗口
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20) // 保留最近20条消息
                .build();
    }
}
