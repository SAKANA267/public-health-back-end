package com.publichealth.public_health_api.module.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequestDTO;
import com.publichealth.public_health_api.module.ai.dto.response.MessageDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDTO;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.entity.AiChatMessage;
import com.publichealth.public_health_api.module.ai.entity.AiSession;
import com.publichealth.public_health_api.module.ai.repository.AiChatMessageRepository;
import com.publichealth.public_health_api.module.ai.repository.AiSessionRepository;
import com.publichealth.public_health_api.module.ai.service.AiSessionService;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI会话服务实现类
 * 负责会话和消息的数据库持久化操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSessionServiceImpl implements AiSessionService {

    private final AiSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final SysUserRepository sysUserRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SessionDTO createSession(String userId, String title) {
        log.info("创建AI会话: userId={}, title={}", userId, title);

        // 验证用户存在
        validateUserExists(userId);

        // 创建会话
        AiSession session = new AiSession();
        session.setUserId(userId);
        session.setTitle(title != null && !title.isEmpty() ? title : "新对话");
        session.setMessageCount(0);
        session.setDeleted(false);

        AiSession savedSession = sessionRepository.save(session);
        log.info("会话创建成功: sessionId={}", savedSession.getId());

        return SessionDTO.fromEntity(savedSession);
    }

    @Override
    @Transactional
    public SessionDTO createSession(CreateSessionRequestDTO request, String userId) {
        String title = request.getTitle();
        if (title == null || title.isEmpty()) {
            title = "新对话";
        }
        return createSession(userId, title);
    }

    @Override
    public List<SessionDTO> getUserSessions(String userId) {
        log.info("获取用户会话列表: userId={}", userId);

        // 按最后消息时间倒序排列
        List<AiSession> sessions = sessionRepository
                .findByUserIdAndDeletedFalseOrderByLastMessageAtDesc(userId);

        return sessions.stream()
                .map(SessionDTO::fromEntity)
                .toList();
    }

    @Override
    public SessionDetailResponse getSessionDetail(String sessionId, String userId) {
        log.info("获取会话详情: sessionId={}, userId={}", sessionId, userId);

        // 验证用户权限
        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        // 获取消息列表
        List<AiChatMessage> messages = messageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        return SessionDetailResponse.fromEntity(session, messages);
    }

    @Override
    public List<MessageDTO> getSessionMessages(String sessionId, String userId) {
        log.info("获取会话消息列表: sessionId={}, userId={}", sessionId, userId);

        // 验证用户权限
        if (!isSessionOwnedByUser(sessionId, userId)) {
            throw new BusinessException("会话不存在或无权访问");
        }

        List<AiChatMessage> messages = messageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        return messages.stream()
                .map(MessageDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MessageDTO saveUserMessage(String sessionId, String userId, String content) {
        log.info("保存用户消息: sessionId={}, userId={}, content={}", sessionId, userId,
                content.length() > 50 ? content.substring(0, 50) + "..." : content);

        // 验证用户权限
        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        // 创建消息
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setRole("user");
        message.setContent(content);
        message.setMessageType("text");

        AiChatMessage savedMessage = messageRepository.save(message);

        // 更新会话信息
        session.incrementMessageCount();
        session.updateLastMessageAt();
        sessionRepository.save(session);

        return MessageDTO.fromEntity(savedMessage);
    }

    @Override
    @Transactional
    public MessageDTO saveAssistantMessage(String sessionId, String userId, String content,
                                            String messageType, String metadata) {
        log.info("保存AI助手消息: sessionId={}, userId={}, messageType={}", sessionId, userId, messageType);

        // 验证用户权限
        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        // 创建消息
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setRole("assistant");
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "text");
        message.setMetadata(metadata);

        AiChatMessage savedMessage = messageRepository.save(message);

        // 更新会话信息
        session.incrementMessageCount();
        session.updateLastMessageAt();
        sessionRepository.save(session);

        return MessageDTO.fromEntity(savedMessage);
    }

    @Override
    @Transactional
    public void updateSessionTitle(String sessionId, String userId, String title) {
        log.info("更新会话标题: sessionId={}, userId={}, title={}", sessionId, userId, title);

        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        session.setTitle(title);
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId, String userId) {
        log.info("删除会话: sessionId={}, userId={}", sessionId, userId);

        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        // 软删除会话
        session.softDelete();
        sessionRepository.save(session);

        // 删除关联的所有消息
        messageRepository.deleteBySessionId(sessionId);

        log.info("会话删除成功: sessionId={}", sessionId);
    }

    @Override
    public boolean isSessionOwnedByUser(String sessionId, String userId) {
        return sessionRepository.existsByIdAndUserIdAndDeletedFalse(sessionId, userId);
    }

    @Override
    @Transactional
    public void updateLastMessageTime(String sessionId, String userId) {
        AiSession session = sessionRepository
                .findByIdAndUserIdAndDeletedFalse(sessionId, userId)
                .orElseThrow(() -> new BusinessException("会话不存在或无权访问"));

        session.updateLastMessageAt();
        sessionRepository.save(session);
    }

    @Override
    public List<String> loadRecentMessagesForContext(String sessionId, String userId, int limit) {
        log.info("加载会话上下文消息: sessionId={}, userId={}, limit={}", sessionId, userId, limit);

        // 验证用户权限
        if (!isSessionOwnedByUser(sessionId, userId)) {
            throw new BusinessException("会话不存在或无权访问");
        }

        // 获取最近的N条消息
        List<AiChatMessage> messages = messageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        // 只返回最近N条
        int startIndex = Math.max(0, messages.size() - limit);
        return messages.subList(startIndex, messages.size())
                .stream()
                .map(AiChatMessage::getContent)
                .toList();
    }

    /**
     * 验证用户存在
     */
    private void validateUserExists(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在（如果sys_user表存在）
        Optional<SysUser> user = sysUserRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BusinessException("用户不存在: " + userId);
        }
    }

    /**
     * 将对象转换为JSON字符串
     */
    private String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return null;
        }
    }
}
