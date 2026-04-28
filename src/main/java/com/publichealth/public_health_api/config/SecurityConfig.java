package com.publichealth.public_health_api.config;

import com.publichealth.public_health_api.security.CustomAccessDeniedHandler;
import com.publichealth.public_health_api.security.JwtAuthenticationEntryPoint;
import com.publichealth.public_health_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * JWT开关配置，可通过 application.properties 中的 jwt.enabled 控制
     * 默认为 true（启用JWT）
     */
    @Value("${jwt.enabled:true}")
    private boolean jwtEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
            .csrf(csrf -> csrf.disable())

            // 配置会话管理为无状态
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // 如果JWT被禁用，允许所有请求通过（用于压力测试）
        if (!jwtEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        // JWT启用时，配置完整的权限控制
        http
            // 配置异常处理
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )

            // 配置请求授权 - RBAC 权限控制
            .authorizeHttpRequests(auth -> auth
                // ========== 公开端点 ==========
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/static/**", "/favicon.ico").permitAll()

                // ========== SUPER_ADMIN 专属 ==========
                .requestMatchers(HttpMethod.DELETE, "/api/users/batch").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/*/password/reset").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/operation-logs/clean").hasRole("SUPER_ADMIN")

                // ========== ADMIN 及以上 ==========
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/*/activate").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/*/deactivate").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/report-cards/batch").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/operation-logs/statistics").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // ========== AUDITOR 及以上 ==========
                .requestMatchers(HttpMethod.PUT, "/api/report-cards/*/approve").hasAnyRole("AUDITOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/report-cards/*/reject").hasAnyRole("AUDITOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/report-cards/*/withdraw").hasAnyRole("AUDITOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/report-cards/pending").hasAnyRole("AUDITOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/operation-logs/**").hasAnyRole("AUDITOR", "ADMIN", "SUPER_ADMIN")

                // ========== USER 及以上（需认证即可） ==========
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/report-cards/**").authenticated()
                .requestMatchers("/api/ai/**").authenticated()
                .requestMatchers("/api/login-history/**").authenticated()

                // ========== 其他所有请求需认证 ==========
                .anyRequest().authenticated()
            )

            // 添加 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
