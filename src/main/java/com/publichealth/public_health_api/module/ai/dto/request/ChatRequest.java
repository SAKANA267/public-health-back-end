package com.publichealth.public_health_api.module.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 聊天请求 DTO
 */
@Data
public class ChatRequest {

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 会话 ID（首次对话可为空）
     */
    private String sessionId;

    /**
     * 上下文信息
     */
    private Context context;

    /**
     * 上下文信息
     */
    @Data
    public static class Context {
        /**
         * 历史消息列表
         */
        private List<Message> previousMessages;

        /**
         * 当前所在页面
         */
        private String currentPage;

        /**
         * 消息信息
         */
        @Data
        public static class Message {
            /**
             * 角色: user, assistant, system
             */
            private String role;

            /**
             * 消息内容
             */
            private String content;
        }
    }
}
