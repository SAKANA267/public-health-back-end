package com.publichealth.public_health_api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求DTO
 * 用于接收用户登录时的请求数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * IP地址（可选）
     * 用于记录登录历史
     */
    private String ipAddress;

    /**
     * 浏览器信息（可选）
     * 用于记录登录历史
     */
    private String userAgent;
}
