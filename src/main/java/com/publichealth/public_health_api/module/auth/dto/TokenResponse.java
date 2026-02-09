package com.publichealth.public_health_api.module.auth.dto;

import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌响应DTO
 * 用于返回登录成功后的令牌信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 访问令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID
         */
        private String id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 真实姓名
         */
        private String name;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 角色
         */
        private SysUser.UserRole role;
    }

    /**
     * 创建令牌响应
     */
    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn, SysUser user) {
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .build();
    }
}
