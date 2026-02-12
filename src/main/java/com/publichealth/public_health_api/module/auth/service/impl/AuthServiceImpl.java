package com.publichealth.public_health_api.module.auth.service.impl;

import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.auth.dto.LoginRequest;
import com.publichealth.public_health_api.module.auth.dto.RefreshTokenRequest;
import com.publichealth.public_health_api.module.auth.dto.RegisterRequest;
import com.publichealth.public_health_api.module.auth.dto.TokenResponse;
import com.publichealth.public_health_api.module.auth.entity.RefreshToken;
import com.publichealth.public_health_api.module.auth.repository.RefreshTokenRepository;
import com.publichealth.public_health_api.module.auth.service.AuthService;
import com.publichealth.public_health_api.module.auth.service.LoginHistoryService;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import com.publichealth.public_health_api.module.sysuser.service.SysUserService;
import com.publichealth.public_health_api.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现类
 * 包含登录、注册、令牌刷新等核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final SysUserRepository sysUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final LoginHistoryService loginHistoryService;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());

        String ipAddress = request.getIpAddress();
        String userAgent = request.getUserAgent();

        try {
            // 1. 验证用户（包括用户名、密码、状态检查）
            SysUser user = sysUserService.validateUser(request.getUsername(), request.getPassword());

            // 2. 生成令牌
            String accessToken = jwtTokenProvider.generateAccessToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name()
            );

            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    user.getId(),
                    user.getUsername()
            );

            // 3. 保存刷新令牌
            saveRefreshToken(user.getId(), refreshToken);

            // 4. 更新最后登录时间
            sysUserService.updateLastLoginTime(user.getId());

            // 5. 记录登录成功历史
            loginHistoryService.recordLoginSuccess(
                    user.getId(),
                    user.getUsername(),
                    ipAddress,
                    userAgent
            );

            log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

            return TokenResponse.of(
                    accessToken,
                    refreshToken,
                    15 * 60L, // 15分钟
                    user
            );
        } catch (BusinessException e) {
            // 记录登录失败历史
            loginHistoryService.recordLoginFailure(
                    null,
                    request.getUsername(),
                    ipAddress,
                    userAgent,
                    e.getMessage()
            );
            throw e;
        }
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("用户注册: username={}", request.getUsername());

        // 1. 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 检查用户名是否已存在
        if (sysUserService.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 检查邮箱是否已存在
        if (request.getEmail() != null && sysUserService.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 4. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(SysUser.UserRole.USER);
        user.setStatus(SysUser.UserStatus.ACTIVE);
        user.setDataScope("SELF"); // 默认只能查看自己的数据
        user.setDeleted(false);

        SysUser createdUser = sysUserRepository.save(user);

        // 5. 生成令牌
        String accessToken = jwtTokenProvider.generateAccessToken(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getRole().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                createdUser.getId(),
                createdUser.getUsername()
        );

        // 6. 保存刷新令牌
        saveRefreshToken(createdUser.getId(), refreshToken);

        log.info("用户注册成功: userId={}, username={}", createdUser.getId(), createdUser.getUsername());

        return TokenResponse.of(
                accessToken,
                refreshToken,
                15 * 60L,
                createdUser
        );
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("刷新令牌");

        String refreshToken = request.getRefreshToken();

        // 1. 验证刷新令牌
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        // 2. 从令牌中获取用户信息
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new BusinessException("令牌中缺少用户信息");
        }

        // 3. 查询刷新令牌记录
        RefreshToken tokenEntity = refreshTokenRepository.findByTokenAndDeletedFalse(refreshToken)
                .orElseThrow(() -> new BusinessException("刷新令牌不存在"));

        // 4. 检查令牌是否过期
        if (tokenEntity.isExpired()) {
            throw new BusinessException("刷新令牌已过期");
        }

        // 5. 获取用户信息
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getStatus() == SysUser.UserStatus.INACTIVE) {
            throw new BusinessException("用户已被禁用");
        }

        // 6. 生成新令牌
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(),
                user.getUsername()
        );

        // 7. 删除旧刷新令牌，保存新刷新令牌
        revokeRefreshToken(refreshToken);
        saveRefreshToken(user.getId(), newRefreshToken);

        log.info("令牌刷新成功: userId={}, username={}", user.getId(), user.getUsername());

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                15 * 60L,
                user
        );
    }

    @Override
    @Transactional
    public void logout(String token) {
        log.info("用户登出");

        if (token != null) {
            // 从访问令牌中获取用户ID
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            if (userId != null) {
                // 撤销用户的所有刷新令牌
                refreshTokenRepository.revokeAllUserTokens(userId);
                log.info("用户登出成功: userId={}", userId);
            }
        }

        // 清除用户上下文
        com.publichealth.public_health_api.context.UserContext.clear();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * 保存刷新令牌
     *
     * @param userId 用户ID
     * @param token  令牌字符串
     */
    private void saveRefreshToken(String userId, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        // 设置过期时间为7天后
        refreshToken.setExpiryTime(LocalDateTime.now().plusDays(7));
        refreshToken.setDeleted(false);

        refreshTokenRepository.save(refreshToken);
        log.debug("保存刷新令牌: userId={}", userId);
    }

    /**
     * 撤销刷新令牌
     *
     * @param token 令牌字符串
     */
    private void revokeRefreshToken(String token) {
        refreshTokenRepository.revokeToken(token);
        log.debug("撤销刷新令牌");
    }
}
