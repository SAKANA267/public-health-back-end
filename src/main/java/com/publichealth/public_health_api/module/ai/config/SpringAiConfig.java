package com.publichealth.public_health_api.module.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Spring AI 配置类
 * 配置 ChatClient Bean，使用外部化的 Prompt 模板和超时参数
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringAiConfig {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;

    @Value("classpath:prompts/assistant-system-prompt.txt")
    private Resource assistantPromptResource;

    @Value("classpath:prompts/intent-system-prompt.txt")
    private Resource intentPromptResource;

    @Value("${ai.assistant.name:小卫}")
    private String assistantName;

    @Value("${ai.entities:reportCard, user, object, audit}")
    private String entityTypes;

    // ZhipuAI 默认超时配置
    private Duration connectTimeout = Duration.ofSeconds(10);
    private Duration readTimeout = Duration.ofSeconds(60);
    private Duration writeTimeout = Duration.ofSeconds(10);

    private String assistantSystemPrompt;
    private String intentSystemPrompt;

    @PostConstruct
    public void init() {
        try {
            byte[] assistantBytes = FileCopyUtils.copyToByteArray(assistantPromptResource.getInputStream());
            String assistantTemplate = new String(assistantBytes, StandardCharsets.UTF_8);
            assistantSystemPrompt = assistantTemplate
                    .replace("{assistant_name}", assistantName)
                    .replace("{entity_types}", entityTypes);

            byte[] intentBytes = FileCopyUtils.copyToByteArray(intentPromptResource.getInputStream());
            intentSystemPrompt = new String(intentBytes, StandardCharsets.UTF_8);

            log.info("Prompt 模板加载成功: assistant_name={}, entities={}", assistantName, entityTypes);
            log.info("AI 调用超时配置: connect={}, read={}, write={}", connectTimeout, readTimeout, writeTimeout);

        } catch (Exception e) {
            log.error("加载 Prompt 模板失败，使用默认配置", e);
            assistantSystemPrompt = getDefaultAssistantPrompt();
            intentSystemPrompt = getDefaultIntentPrompt();
        }
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(assistantSystemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                
                                .build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Bean("intentRecognitionClient")
    public ChatClient intentRecognitionClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(intentSystemPrompt)
                .build();
    }

    private String getDefaultAssistantPrompt() {
        return "你是公共卫生管理系统的 AI 助手，名字叫\"%s\"。请用友好、专业、简洁的语气回复。".formatted(assistantName);
    }

    private String getDefaultIntentPrompt() {
        return "你是意图识别助手。请分析用户输入，识别意图和实体。请以 JSON 格式返回。";
    }
}
