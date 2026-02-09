package com.publichealth.public_health_api.module.auth.service;

import com.publichealth.public_health_api.module.auth.dto.LoginRequest;
import com.publichealth.public_health_api.module.auth.dto.RefreshTokenRequest;
import com.publichealth.public_health_api.module.auth.dto.RegisterRequest;
import com.publichealth.public_health_api.module.auth.dto.TokenResponse;

/**
 * 认证服务接口
 * 定义认证相关的业务操作
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 令牌响应
     */
    TokenResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 令牌响应
     */
    TokenResponse register(RegisterRequest request);

    /**
     * 刷新令牌
     *
     * @param request 刷新令牌请求
     * @return 新的令牌响应
     */
    TokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * 用户登出
     *
     * @param token 刷新令牌
     */
    void logout(String token);

    /**
     * 验证令牌
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
}
