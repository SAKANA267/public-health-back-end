package com.publichealth.public_health_api.module.ai.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 * 定义 AI 助手返回给前端的不同操作类型
 */
@Getter
public enum ActionType {
    NAVIGATE("navigate", "页面跳转"),
    API("api", "API调用"),
    CALLBACK("callback", "前端回调");

    private final String code;
    private final String description;

    ActionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ActionType fromCode(String code) {
        for (ActionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
