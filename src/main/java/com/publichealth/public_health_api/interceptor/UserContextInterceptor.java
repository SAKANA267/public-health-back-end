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
 */
@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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

        log.debug("设置用户上下文: userId={}, username={}", userId, username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
