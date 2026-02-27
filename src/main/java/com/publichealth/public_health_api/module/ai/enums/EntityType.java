package com.publichealth.public_health_api.module.ai.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 实体类型枚举
 * 定义 AI 助手可以操作的业务实体类型
 */
@Getter
public enum EntityType {
    REPORT_CARD("reportCard", "报告卡", Arrays.asList("报告卡", "报卡", "报告", "card")),
    USER("user", "用户", Arrays.asList("用户", "user", "账号")),
    OBJECT("object", "对象", Arrays.asList("对象", "object")),
    AUDIT("audit", "审核", Arrays.asList("审核", "audit", "待审核"));

    private final String code;
    private final String name;
    private final List<String> aliases;

    EntityType(String code, String name, List<String> aliases) {
        this.code = code;
        this.name = name;
        this.aliases = aliases;
    }

    public static EntityType fromInput(String input) {
        if (input == null) {
            return null;
        }
        for (EntityType type : values()) {
            if (type.aliases.contains(input) || type.code.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }

    public static String getAllAliasesString() {
        return String.join(",", Arrays.stream(values())
                .flatMap(e -> e.aliases.stream())
                .toList());
    }
}
