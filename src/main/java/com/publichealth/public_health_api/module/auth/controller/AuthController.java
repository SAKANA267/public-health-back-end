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
     * @param loginRequest 登录请求
     * @param httpRequest HTTP 请求
     * @return 令牌响应
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletRequest httpRequest) {
        log.info("收到登录请求: username={}", loginRequest.getUsername());

        // 从请求中提取IP地址和User-Agent
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        // 设置到请求对象中
        loginRequest.setIpAddress(ipAddress);
        loginRequest.setUserAgent(userAgent);

        TokenResponse response = authService.login(loginRequest);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 提取客户端真实IP地址
     * 支持代理和负载均衡场景
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况（X-Forwarded-For可能包含多个IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
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
