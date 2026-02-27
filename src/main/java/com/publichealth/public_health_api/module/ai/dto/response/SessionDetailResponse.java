package com.publichealth.public_health_api.module.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会话详情响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailResponse {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 消息列表
     */
    private List<MessageInfo> messages;

    /**
     * 消息信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageInfo {
        /**
         * 消息 ID
         */
        private String id;

        /**
         * 角色
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;

        /**
         * 时间戳
         */
        private Long timestamp;
    }
}
