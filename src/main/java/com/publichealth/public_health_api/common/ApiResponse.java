package com.publichealth.public_health_api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应格式
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应状态码
     * 200-成功, 400-客户端错误, 500-服务器错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    // ============================================
    // 成功响应快捷方法
    // ============================================

    /**
     * 成功响应 (无数据)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null, System.currentTimeMillis());
    }

    /**
     * 成功响应 (带数据)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data, System.currentTimeMillis());
    }

    /**
     * 成功响应 (自定义消息)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, System.currentTimeMillis());
    }

    // ============================================
    // 失败响应快捷方法
    // ============================================

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应 (指定状态码)
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * 客户端错误 (400)
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null, System.currentTimeMillis());
    }

    /**
     * 资源未找到 (404)
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null, System.currentTimeMillis());
    }

    /**
     * 未授权 (401)
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null, System.currentTimeMillis());
    }

    /**
     * 禁止访问 (403)
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null, System.currentTimeMillis());
    }
}
