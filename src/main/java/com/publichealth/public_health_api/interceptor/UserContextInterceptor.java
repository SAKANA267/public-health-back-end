package com.publichealth.public_health_api.interceptor;

import com.publichealth.public_health_api.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器
 * 从请求头提取用户信息并存入ThreadLocal
 * 注意：JWT 认证过滤器会在认证通过后设置 UserContext
 * 此拦截器作为备用方案，仅在未通过 JWT 认证时使用
 */
@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果 UserContext 已经被 JWT 过滤器设置，则跳过
        if (UserContext.getUserId() != null && UserContext.getUsername() != null) {
            log.debug("用户上下文已由 JWT 过滤器设置: userId={}, username={}",
                    UserContext.getUserId(), UserContext.getUsername());
            return true;
        }

        // 备用方案：从请求头中提取用户信息（用于开发模式或其他非 JWT 认证场景）
        String userId = request.getHeader(HEADER_USER_ID);
        String username = request.getHeader(HEADER_USERNAME);

        // 如果请求头中没有用户信息，设置默认值（开发模式）
        if (userId == null || userId.isEmpty()) {
            userId = "system";
        }
        if (username == null || username.isEmpty()) {
            username = "system";
        }

        UserContext.setUserId(userId);
        UserContext.setUsername(username);

        log.debug("设置用户上下文（备用方案）: userId={}, username={}", userId, username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
