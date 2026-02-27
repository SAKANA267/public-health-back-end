package com.publichealth.public_health_api.module.ai.enums;

import lombok.Getter;

/**
 * 意图类型枚举
 * 定义 AI 助手支持的各种用户意图
 */
@Getter
public enum IntentType {
    NAVIGATE("navigate", "页面导航"),
    CREATE("create", "创建数据"),
    READ("read", "读取数据"),
    UPDATE("update", "更新数据"),
    DELETE("delete", "删除数据"),
    QUERY("query", "查询数据"),
    COUNT("count", "统计数量"),
    HELP("help", "帮助说明"),
    UNKNOWN("unknown", "未知意图");

    private final String code;
    private final String description;

    IntentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static IntentType fromCode(String code) {
        for (IntentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
