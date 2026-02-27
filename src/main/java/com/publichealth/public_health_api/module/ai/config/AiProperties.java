package com.publichealth.public_health_api.module.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模块配置属性
 * 从 application.yml 读取 ai.* 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private Llm llm = new Llm();
    private Session session = new Session();

    /**
     * LLM 服务配置
     */
    @Data
    public static class Llm {
        /**
         * LLM 提供商: qianwen, openai, baidu
         */
        private String provider = "qianwen";

        /**
         * API 密钥
         */
        private String apiKey;

        /**
         * API 基础 URL
         */
        private String baseUrl;

        /**
         * 模型名称
         */
        private String model = "qwen-turbo";

        /**
         * 请求超时时间（毫秒）
         */
        private Integer timeout = 30000;

        /**
         * 最大 token 数
         */
        private Integer maxTokens = 2000;

        /**
         * 温度参数（0-1）
         */
        private Double temperature = 0.7;
    }

    /**
     * 会话配置
     */
    @Data
    public static class Session {
        /**
         * 最大历史消息数量
         */
        private Integer maxHistoryMessages = 20;

        /**
         * 会话过期时间（小时）
         */
        private Integer sessionExpireHours = 72;
    }
}
