package com.publichealth.public_health_api.module.ai.client.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.module.ai.client.LlmClient;
import com.publichealth.public_health_api.module.ai.config.AiProperties;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.enums.EntityType;
import com.publichealth.public_health_api.module.ai.enums.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * LLM 客户端实现类
 * 基于 LLM + 规则混合模式实现意图识别
 *
 * 优先级策略：
 * 1. 优先使用智谱AI LLM 进行意图识别
 * 2. LLM 不可用或返回未知意图时，降级到规则匹配
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClientImpl implements LlmClient {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    /**
     * 智谱AI客户端（懒加载）
     */
    private ZhipuAiClient zhipuClient;

    /**
     * 意图识别的System Prompt
     */
    private static final String INTENT_SYSTEM_PROMPT = """
            你是一个公共卫生管理系统的 AI 助手。请分析用户输入，识别意图并提取参数。

            支持的意图类型：
            - navigate: 页面导航
            - create: 创建数据
            - read: 读取数据
            - update: 更新数据
            - delete: 删除数据
            - query: 查询数据
            - count: 统计数量
            - help: 帮助说明

            支持的实体类型：
            - reportCard: 报告卡（别名：报告卡、报卡、报告、card）
            - user: 用户（别名：用户、user、账号）
            - object: 对象（别名：对象、object）
            - audit: 审核（别名：审核、audit、待审核）

            请以 JSON 格式返回，不要包含其他内容：
            {
              "intent": "意图类型",
              "entity": "实体类型代码",
              "params": {提取的参数},
              "confidence": 0.95
            }
            """;

    @Override
    public IntentResponse recognizeIntent(String userMessage, List<ChatContext> history) {
        // 1. 优先使用 LLM 进行意图识别
        IntentResponse llmResult = callLlmForIntent(userMessage);

        // 2. LLM 返回有效结果时直接返回
        if (llmResult != null && llmResult.getIntent() != IntentType.UNKNOWN && llmResult.getConfidence() > 0.3) {
            log.info("LLM意图识别成功: intent={}, confidence={}", llmResult.getIntent(), llmResult.getConfidence());
            return llmResult;
        }

        // 3. LLM 不可用或返回未知意图时，降级到规则匹配
        log.info("LLM不可用或识别失败，降级使用规则匹配");
        IntentResponse ruleResult = matchByRules(userMessage);
        if (ruleResult != null) {
            return ruleResult;
        }

        // 4. 都无法识别时返回 LLM 的结果（即使是 UNKNOWN）
        return llmResult != null ? llmResult : IntentResponse.builder()
                .intent(IntentType.UNKNOWN)
                .confidence(0.0)
                .params(Map.of("originalMessage", userMessage))
                .build();
    }

    @Override
    public String generateChatReply(String userMessage, IntentResponse intent) {
        // 对于已识别的明确意图，使用预设回复
        if (intent.getIntent() != IntentType.UNKNOWN && intent.getConfidence() > 0.7) {
            return switch (intent.getIntent()) {
                case NAVIGATE -> String.format("正在为您跳转到 %s 页面...",
                        intent.getEntity() != null ? intent.getEntity().getName() : "目标");
                case CREATE -> String.format("已为您打开 %s 添加表单",
                        intent.getEntity() != null ? intent.getEntity().getName() : "");
                case QUERY -> "为您查询相关数据...";
                case COUNT -> String.format("当前共有 %d 条数据", 0); // 需要实际查询
                case HELP -> """
                        我可以帮您：
                        - 导航到各个页面
                        - 创建、查询、修改数据
                        - 统计数据信息
                        请告诉我您需要什么帮助？
                        """;
                default -> "抱歉，我没有理解您的意思，请换一种说法。";
            };
        }

        // 对于未知意图，调用 LLM 生成回复
        String llmReply = callLlmForChatReply(userMessage, intent);
        return llmReply != null ? llmReply : "抱歉，我没有理解您的意思，请换一种说法。";
    }

    @Override
    public List<String> generateSuggestions(IntentType intent, String entity) {
        return switch (intent) {
            case NAVIGATE -> List.of("查看详情", "导出数据", "返回");
            case CREATE -> List.of("继续添加", "查看列表", "返回");
            case QUERY -> List.of("筛选数据", "导出报表", "新增数据");
            case COUNT -> List.of("查看列表", "统计分析", "导出报表");
            default -> List.of("查看帮助");
        };
    }

    /**
     * 规则匹配意图识别
     */
    private IntentResponse matchByRules(String message) {
        String msg = message.toLowerCase().trim();

        // 导航意图
        if (msg.contains("打开") || msg.contains("跳转") || msg.contains("去")) {
            EntityType entity = extractEntity(message);
            return IntentResponse.builder()
                    .intent(IntentType.NAVIGATE)
                    .entity(entity)
                    .params(Map.of("originalMessage", message))
                    .confidence(0.9)
                    .build();
        }

        // 创建意图
        if (msg.contains("新增") || msg.contains("创建") || msg.contains("添加")) {
            EntityType entity = extractEntity(message);
            Map<String, Object> params = extractCreateParams(message);
            return IntentResponse.builder()
                    .intent(IntentType.CREATE)
                    .entity(entity)
                    .params(params)
                    .confidence(0.85)
                    .build();
        }

        // 查询意图
        if (msg.contains("显示") || msg.contains("查看") || msg.contains("查询")) {
            EntityType entity = extractEntity(message);
            Map<String, Object> params = extractQueryParams(message);
            return IntentResponse.builder()
                    .intent(IntentType.QUERY)
                    .entity(entity)
                    .params(params)
                    .confidence(0.85)
                    .build();
        }

        // 统计意图
        if (msg.contains("多少") || msg.contains("统计") || msg.contains("数量")) {
            EntityType entity = extractEntity(message);
            return IntentResponse.builder()
                    .intent(IntentType.COUNT)
                    .entity(entity)
                    .params(Map.of())
                    .confidence(0.9)
                    .build();
        }

        // 帮助意图
        if (msg.contains("帮助") || msg.contains("能做什么") || msg.contains("怎么用")) {
            return IntentResponse.builder()
                    .intent(IntentType.HELP)
                    .params(Map.of())
                    .confidence(0.95)
                    .build();
        }

        return null;
    }

    /**
     * 从消息中提取实体类型
     */
    private EntityType extractEntity(String message) {
        for (EntityType type : EntityType.values()) {
            for (String alias : type.getAliases()) {
                if (message.contains(alias)) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * 提取创建参数
     */
    private Map<String, Object> extractCreateParams(String message) {
        Map<String, Object> params = new HashMap<>();

        // 简单提取姓名
        if (message.contains("姓名") || message.contains("叫")) {
            int idx = Math.max(message.lastIndexOf("姓名"), message.lastIndexOf("叫"));
            if (idx > 0) {
                String after = message.substring(idx + 1);
                String name = after.split("[，,。\\.\\s]")[0].trim();
                if (!name.isEmpty()) {
                    params.put("name", name);
                }
            }
        }

        return params;
    }

    /**
     * 提取查询参数
     */
    private Map<String, Object> extractQueryParams(String message) {
        Map<String, Object> params = new HashMap<>();

        // 提取状态
        if (message.contains("待审核") || message.contains("未审核")) {
            params.put("status", "pending");
        } else if (message.contains("已审核")) {
            params.put("status", "approved");
        } else if (message.contains("已拒绝")) {
            params.put("status", "rejected");
        }

        return params;
    }

    /**
     * 调用智谱AI API 进行意图识别
     * 失败时返回 null 以触发规则匹配降级
     */
    private IntentResponse callLlmForIntent(String userMessage) {
        log.info("尝试使用智谱AI进行意图识别: {}", userMessage);

        try {
            ZhipuAiClient client = getZhipuClient();
            if (client == null) {
                log.warn("智谱AI客户端不可用，返回null");
                return null;
            }

            // 构建请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model(aiProperties.getLlm().getModel())
                    .messages(Arrays.asList(
                            ChatMessage.builder()
                                    .role(ChatMessageRole.SYSTEM.value())
                                    .content(INTENT_SYSTEM_PROMPT)
                                    .build(),
                            ChatMessage.builder()
                                    .role(ChatMessageRole.USER.value())
                                    .content(userMessage)
                                    .build()
                    ))
                    .temperature(aiProperties.getLlm().getTemperature().floatValue())
                    .maxTokens(aiProperties.getLlm().getMaxTokens())
                    .stream(false)
                    .build();

            // 调用API
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            // 解析响应
            if (response != null && response.getData() != null
                    && !response.getData().getChoices().isEmpty()) {
                Object contentObj = response.getData().getChoices().get(0).getMessage().getContent();
                String content = contentObj != null ? String.valueOf(contentObj) : "";
                log.info("智谱AI响应: {}", content);
                return parseIntentResponse(content);
            }

        } catch (Exception e) {
            log.error("智谱AI调用失败，将降级使用规则匹配: {}", e.getMessage());
        }

        // 返回 null 触发规则匹配降级
        return null;
    }

    /**
     * 调用智谱AI生成聊天回复
     * 失败时返回 null
     */
    private String callLlmForChatReply(String userMessage, IntentResponse intent) {
        log.info("尝试使用智谱AI生成聊天回复");

        try {
            ZhipuAiClient client = getZhipuClient();
            if (client == null) {
                log.warn("智谱AI客户端不可用，无法生成聊天回复");
                return null;
            }

            String systemPrompt = """
                    你是一个公共卫生管理系统的 AI 助手，友好、专业、简洁。
                    请用自然、友好的语气回复用户，回复要简洁明了。
                    """;

            // 构建请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model(aiProperties.getLlm().getModel())
                    .messages(Arrays.asList(
                            ChatMessage.builder()
                                    .role(ChatMessageRole.SYSTEM.value())
                                    .content(systemPrompt)
                                    .build(),
                            ChatMessage.builder()
                                    .role(ChatMessageRole.USER.value())
                                    .content(userMessage)
                                    .build()
                    ))
                    .temperature(0.8f)
                    .maxTokens(500)
                    .stream(false)
                    .build();

            // 调用API
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            // 解析响应
            if (response != null && response.getData() != null
                    && !response.getData().getChoices().isEmpty()) {
                Object contentObj = response.getData().getChoices().get(0).getMessage().getContent();
                return contentObj != null ? String.valueOf(contentObj) : null;
            }

        } catch (Exception e) {
            log.error("智谱AI聊天回复生成失败: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 解析意图识别响应
     */
    private IntentResponse parseIntentResponse(String jsonContent) {
        try {
            // 清理可能的markdown代码块标记
            String cleaned = jsonContent.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            // 解析JSON
            Map<String, Object> result = objectMapper.readValue(cleaned, Map.class);

            String intentStr = (String) result.get("intent");
            String entityStr = (String) result.get("entity");
            Map<String, Object> params = (Map<String, Object>) result.getOrDefault("params", new HashMap<>());
            Double confidence = ((Number) result.getOrDefault("confidence", 0.5)).doubleValue();

            IntentType intent = IntentType.fromCode(intentStr);
            EntityType entity = entityStr != null ? EntityType.fromInput(entityStr) : null;

            return IntentResponse.builder()
                    .intent(intent)
                    .entity(entity)
                    .params(params)
                    .confidence(confidence)
                    .build();

        } catch (JsonProcessingException e) {
            log.warn("解析意图响应失败: {}", jsonContent, e);
        }

        return IntentResponse.builder()
                .intent(IntentType.UNKNOWN)
                .confidence(0.0)
                .build();
    }

    /**
     * 获取或创建智谱AI客户端
     * 如果 API Key 未配置，返回 null
     */
    private ZhipuAiClient getZhipuClient() {
        if (zhipuClient == null) {
            String apiKey = aiProperties.getLlm().getApiKey();
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key")) {
                log.warn("智谱AI API Key未配置，将降级使用规则匹配");
                return null;
            }

            try {
                zhipuClient = ZhipuAiClient.builder()
                        .ofZHIPU()
                        .apiKey(apiKey)
                        .build();
                log.info("智谱AI客户端初始化成功，model={}", aiProperties.getLlm().getModel());
            } catch (Exception e) {
                log.error("智谱AI客户端初始化失败", e);
                return null;
            }
        }
        return zhipuClient;
    }
}
