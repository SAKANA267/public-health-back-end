package com.publichealth.public_health_api.module.ai.dto;

/**
 * 意图识别结果 - Spring AI 结构化输出
 * 使用 record 实现，LLM 会自动将输出映射到此类型
 */
public record IntentResult(
        String intent,      // navigate, create, read, update, delete, query, count, help
        String entity,      // reportCard, user, object, audit
        Params params,
        Double confidence
) {
    /**
     * 参数详情
     */
    public record Params(
            Long id,
            String name,
            String status,
            String startDate,
            String endDate
            // 其他可能的参数...
    ) {}
}
