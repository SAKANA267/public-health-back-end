package com.publichealth.public_health_api.module.auth.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryQueryRequest;
import com.publichealth.public_health_api.module.auth.dto.LoginHistoryResponse;
import com.publichealth.public_health_api.module.auth.entity.LoginHistory;
import com.publichealth.public_health_api.module.auth.repository.LoginHistoryRepository;
import com.publichealth.public_health_api.module.auth.service.LoginHistoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录历史服务实现类
 * 包含登录历史记录、查询、统计等核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    // ============================================
    // 记录登录历史
    // ============================================

    @Override
    @Transactional
    public void recordLoginSuccess(String userId, String username, String ipAddress, String userAgent) {
        recordLoginSuccess(userId, username, ipAddress, userAgent, null);
    }

    @Override
    @Transactional
    public void recordLoginSuccess(String userId, String username, String ipAddress, String userAgent, String loginLocation) {
        log.info("记录登录成功: userId={}, username={}, ip={}", userId, username, ipAddress);

        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUserId(userId);
        loginHistory.setUsername(username);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setUserAgent(userAgent);
        loginHistory.setLoginLocation(loginLocation);
        loginHistory.setStatus(LoginHistory.LoginStatus.SUCCESS);
        loginHistory.setLoginTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);
        log.debug("登录成功记录已保存: id={}", loginHistory.getId());
    }

    @Override
    @Transactional
    public void recordLoginFailure(String username, String ipAddress, String userAgent, String failReason) {
        recordLoginFailure(null, username, ipAddress, userAgent, failReason);
    }

    @Override
    @Transactional
    public void recordLoginFailure(String userId, String username, String ipAddress, String userAgent, String failReason) {
        log.info("记录登录失败: username={}, ip={}, reason={}", username, ipAddress, failReason);

        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUserId(userId);
        loginHistory.setUsername(username);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setUserAgent(userAgent);
        loginHistory.setStatus(LoginHistory.LoginStatus.FAILURE);
        loginHistory.setFailReason(failReason);
        loginHistory.setLoginTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);
        log.debug("登录失败记录已保存: id={}", loginHistory.getId());
    }

    // ============================================
    // 查询登录历史
    // ============================================

    @Override
    public List<LoginHistoryResponse> getLoginHistoryByUserId(String userId) {
        log.debug("查询用户登录历史: userId={}", userId);

        List<LoginHistory> histories = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
        return histories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<LoginHistoryResponse> getLoginHistoryWithPaging(LoginHistoryQueryRequest request) {
        log.debug("分页查询登录历史: userId={}, page={}, size={}",
                request.getUserId(), request.getPage(), request.getSize());

        // 创建分页参数，按登录时间倒序
        Pageable pageable = PageRequest.of(
                request.getPage() - 1, // Spring Data JPA 页码从0开始
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "loginTime")
        );

        Page<LoginHistory> page;

        // 根据查询条件构建查询
        if (request.getStatus() != null) {
            page = loginHistoryRepository.findByUserIdAndStatusOrderByLoginTimeDesc(
                    request.getUserId(),
                    request.getStatus(),
                    pageable
            );
        } else if (request.getStartTime() != null && request.getEndTime() != null) {
            page = loginHistoryRepository.findByUserIdAndTimeRange(
                    request.getUserId(),
                    request.getStartTime(),
                    request.getEndTime(),
                    pageable
            );
        } else {
            page = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(
                    request.getUserId(),
                    pageable
            );
        }

        List<LoginHistoryResponse> responses = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                responses
        );
    }

    @Override
    public List<LoginHistoryResponse> getRecentLoginsByUserId(String userId, int limit) {
        log.debug("查询最近登录记录: userId={}, limit={}", userId, limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "loginTime"));
        List<LoginHistory> histories = loginHistoryRepository.findRecentByUserId(userId, pageable);

        return histories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // 统计查询
    // ============================================

    @Override
    public long countLoginsByUserId(String userId) {
        long count = loginHistoryRepository.countByUserId(userId);
        log.debug("统计用户登录次数: userId={}, count={}", userId, count);
        return count;
    }

    @Override
    public long countFailedLoginsInPeriod(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        long count = loginHistoryRepository.countFailedLoginsInPeriod(userId, startTime, endTime);
        log.debug("统计用户登录失败次数: userId={}, startTime={}, endTime={}, count={}",
                userId, startTime, endTime, count);
        return count;
    }

    @Override
    public LoginHistoryResponse getLastSuccessLogin(String userId) {
        log.debug("查询最后一次成功登录: userId={}", userId);

        Pageable pageable = PageRequest.of(0, 1);
        List<LoginHistory> histories = loginHistoryRepository.findLastSuccessLogin(userId, pageable);

        return histories.isEmpty() ? null : toResponse(histories.get(0));
    }

    // ============================================
    // 删除操作
    // ============================================

    @Override
    @Transactional
    public int deleteLoginHistoryBefore(LocalDateTime beforeTime) {
        log.info("删除指定时间之前的登录历史: beforeTime={}", beforeTime);

        // 使用原生SQL删除，确保性能
        List<LoginHistory> toDelete = loginHistoryRepository.findAll().stream()
                .filter(h -> h.getLoginTime().isBefore(beforeTime))
                .collect(Collectors.toList());

        loginHistoryRepository.deleteAll(toDelete);
        log.info("已删除 {} 条登录历史记录", toDelete.size());
        return toDelete.size();
    }

    @Override
    @Transactional
    public int deleteLoginHistoryByUserId(String userId) {
        log.info("删除用户登录历史: userId={}", userId);

        List<LoginHistory> toDelete = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
        loginHistoryRepository.deleteAll(toDelete);
        log.info("已删除用户 {} 的 {} 条登录历史记录", userId, toDelete.size());
        return toDelete.size();
    }

    // ============================================
    // 私有方法
    // ============================================

    /**
     * 将实体转换为响应DTO
     *
     * @param entity 登录历史实体
     * @return 登录历史响应DTO
     */
    private LoginHistoryResponse toResponse(LoginHistory entity) {
        return LoginHistoryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .loginTime(entity.getLoginTime())
                .loginLocation(entity.getLoginLocation())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .status(entity.getStatus())
                .failReason(entity.getFailReason())
                .build();
    }
}
