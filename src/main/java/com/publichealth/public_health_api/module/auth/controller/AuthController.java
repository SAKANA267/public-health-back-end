package com.publichealth.public_health_api.module.auth.controller;

import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.module.auth.dto.LoginRequest;
import com.publichealth.public_health_api.module.auth.dto.RefreshTokenRequest;
import com.publichealth.public_health_api.module.auth.dto.RegisterRequest;
import com.publichealth.public_health_api.module.auth.dto.TokenResponse;
import com.publichealth.public_health_api.module.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理登录、注册、令牌刷新等HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ============================================
    // 认证操作
    // ============================================

    /**
     * 用户登录
     * POST /api/auth/login
     *
     * @param request 登录请求
     * @return 令牌响应
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: username={}", request.getUsername());
        TokenResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 用户注册
     * POST /api/auth/register
     *
     * @param request 注册请求
     * @return 令牌响应
     */
    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求: username={}", request.getUsername());
        TokenResponse response = authService.register(request);
        return ApiResponse.success("注册成功", response);
    }

    /**
     * 刷新令牌
     * POST /api/auth/refresh
     *
     * @param request 刷新令牌请求
     * @return 新的令牌响应
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("收到刷新令牌请求");
        TokenResponse response = authService.refreshToken(request);
        return ApiResponse.success("令牌刷新成功", response);
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     *
     * @param httpRequest HTTP 请求
     * @return 成功响应
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        // 从请求头中获取令牌
        String authHeader = httpRequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        log.info("收到登出请求");
        authService.logout(token);
        return ApiResponse.success("登出成功");
    }

    /**
     * 验证令牌
     * GET /api/auth/validate
     *
     * @param httpRequest HTTP 请求
     * @return 验证结果
     */
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateToken(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            return ApiResponse.success(isValid);
        }
        return ApiResponse.success(false);
    }
}
