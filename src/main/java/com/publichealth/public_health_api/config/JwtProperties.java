package com.publichealth.public_health_api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * 从 application.properties 读取配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret = "public-health-api-secret-key-change-in-production";

    /**
     * 访问令牌过期时间（毫秒）
     * 默认 15 分钟
     */
    private Long accessTokenExpiration = 15 * 60 * 1000L;

    /**
     * 刷新令牌过期时间（毫秒）
     * 默认 7 天
     */
    private Long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 令牌发行者
     */
    private String issuer = "public-health-api";

    /**
     * 访问令牌过期时间（秒） - 用于响应
     */
    public Long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }
}
