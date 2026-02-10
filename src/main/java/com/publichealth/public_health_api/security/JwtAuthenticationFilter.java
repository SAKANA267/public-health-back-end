package com.publichealth.public_health_api.security;

import com.publichealth.public_health_api.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 从请求头中提取并验证 JWT 令牌
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中提取 JWT 令牌
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 从令牌中获取用户信息
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                // 设置用户上下文
                UserContext.setUserId(userId);
                UserContext.setUsername(username);

                // 设置 Spring Security 认证信息
                if (StringUtils.hasText(userId)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    role != null ?
                                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)) :
                                            Collections.emptyList()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("用户认证成功: userId={}, username={}, role={}", userId, username, role);
                }
            } else {
                log.debug("请求未携带有效的 JWT 令牌: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("JWT 认证失败: {}", e.getMessage());
            // 清除认证信息
            SecurityContextHolder.clearContext();
            UserContext.clear();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取令牌
     *
     * @param request HTTP 请求
     * @return JWT 令牌，如果不存在则返回 null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 对以下路径不进行 JWT 认证
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
