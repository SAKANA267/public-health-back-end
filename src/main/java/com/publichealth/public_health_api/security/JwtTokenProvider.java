package com.publichealth.public_health_api.security;

import com.publichealth.public_health_api.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌提供者
 * 负责生成和验证 JWT 令牌
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色
     * @return JWT 令牌
     */
    public String generateAccessToken(String userId, String username, String role) {
        return generateToken(userId, username, role, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * 生成刷新令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 刷新令牌
     */
    public String generateRefreshToken(String userId, String username) {
        return generateToken(userId, username, null, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * 生成令牌
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param role        用户角色（可为空）
     * @param expiration  过期时间（毫秒）
     * @return JWT 令牌
     */
    private String generateToken(String userId, String username, String role, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        if (role != null) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtProperties.getIssuer())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT 令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从令牌中获取用户角色
     *
     * @param token JWT 令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    /**
     * 从令牌中获取所有声明
     *
     * @param token JWT 令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("解析 JWT 令牌失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证令牌
     *
     * @param token JWT 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("JWT 签名无效: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT 令牌格式无效: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 JWT 令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 令牌为空: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 获取令牌过期时间
     *
     * @param token JWT 令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 检查令牌是否过期
     *
     * @param token JWT 令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
}
