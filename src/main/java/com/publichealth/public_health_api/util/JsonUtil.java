package com.publichealth.public_health_api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON序列化工具类
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return "{}";
        }
    }

    /**
     * 对象转格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return "{}";
        }
    }

    /**
     * 对象转JSON字符串，并限制最大长度
     *
     * @param obj     对象
     * @param maxLength 最大长度
     * @return JSON字符串（可能被截断）
     */
    public static String toJson(Object obj, int maxLength) {
        String json = toJson(obj);
        if (json == null) {
            return null;
        }
        if (json.length() > maxLength) {
            return json.substring(0, maxLength) + "...";
        }
        return json;
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
