package com.publichealth.public_health_api.config;

import com.publichealth.public_health_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 配置 JWT 认证过滤器和端点权限
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
            .csrf(csrf -> csrf.disable())

            // 配置会话管理为无状态
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 公开端点 - 认证相关
                .requestMatchers("/api/auth/**").permitAll()

                // 公开端点 - Actuator（开发环境）
                .requestMatchers("/actuator/**").permitAll()

                // 公开端点 - Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 公开端点 - 静态资源
                .requestMatchers("/", "/static/**", "/favicon.ico").permitAll()

                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )

            // 添加 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
