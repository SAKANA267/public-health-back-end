package com.publichealth.public_health_api.module.ai.service.impl;

import com.publichealth.public_health_api.module.ai.dto.request.CreateSessionRequest;
import com.publichealth.public_health_api.module.ai.dto.response.SessionDetailResponse;
import com.publichealth.public_health_api.module.ai.entity.AiSession;
import com.publichealth.public_health_api.module.ai.entity.ChatMessage;
import com.publichealth.public_health_api.module.ai.repository.AiSessionRepository;
import com.publichealth.public_health_api.module.ai.repository.ChatMessageRepository;
import com.publichealth.public_health_api.module.ai.service.SessionManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 会话管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManagerServiceImpl implements SessionManagerService {

    private final AiSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Override
    @Transactional
    public String createSession(CreateSessionRequest request) {
        AiSession session = AiSession.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .messageCount(0)
                .build();
        session = sessionRepository.save(session);
        log.info("创建新会话: sessionId={}, userId={}", session.getId(), request.getUserId());
        return session.getId();
    }

    @Override
    public SessionDetailResponse getSessionDetail(String sessionId) {
        AiSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));

        List<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        List<SessionDetailResponse.MessageInfo> messageInfos = messages.stream()
                .map(msg -> SessionDetailResponse.MessageInfo.builder()
                        .id(msg.getId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .timestamp(msg.getCreatedAt().toEpochSecond(java.time.ZoneOffset.of("+8")))
                        .build())
                .toList();

        return SessionDetailResponse.builder()
                .sessionId(session.getId())
                .title(session.getTitle())
                .messages(messageInfos)
                .build();
    }

    @Override
    @Transactional
    public void addMessage(String sessionId, String role, String content, String metadata) {
        ChatMessage message = ChatMessage.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .metadata(metadata)
                .build();
        messageRepository.save(message);

        // 更新会话的消息数量和最后消息时间
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setMessageCount(session.getMessageCount() + 1);
            session.setLastMessageAt(LocalDateTime.now());
            sessionRepository.save(session);
        });

        log.debug("添加消息到会话: sessionId={}, role={}", sessionId, role);
    }

    @Override
    public List<ChatMessage> getSessionHistory(String sessionId, int limit) {
        List<ChatMessage> allMessages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        if (allMessages.size() <= limit) {
            return allMessages;
        }
        // 返回最近 limit 条消息
        return allMessages.subList(allMessages.size() - limit, allMessages.size());
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId) {
        sessionRepository.softDelete(sessionId);
        log.info("删除会话: sessionId={}", sessionId);
    }

    @Override
    @Transactional
    public String getOrCreateSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            return sessionRepository.findById(sessionId)
                    .map(AiSession::getId)
                    .orElseGet(() -> createNewSession());
        }
        return createNewSession();
    }

    private String createNewSession() {
        AiSession session = AiSession.builder()
                .userId("system") // 默认用户，后续可以从上下文获取
                .title("新对话")
                .messageCount(0)
                .build();
        session = sessionRepository.save(session);
        return session.getId();
    }
}
