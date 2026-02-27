package com.publichealth.public_health_api.module.ai.service.impl;

import com.publichealth.public_health_api.module.ai.client.LlmClient;
import com.publichealth.public_health_api.module.ai.dto.response.IntentResponse;
import com.publichealth.public_health_api.module.ai.service.IntentRecognizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 意图识别服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentRecognizerServiceImpl implements IntentRecognizerService {

    private final LlmClient llmClient;

    @Override
    public IntentResponse recognize(String userMessage) {
        return recognize(userMessage, List.of());
    }

    @Override
    public IntentResponse recognize(String userMessage, List<LlmClient.ChatContext> history) {
        log.info("识别用户意图: message={}", userMessage);
        return llmClient.recognizeIntent(userMessage, history);
    }
}
