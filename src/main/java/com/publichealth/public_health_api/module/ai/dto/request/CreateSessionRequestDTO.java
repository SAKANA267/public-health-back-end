package com.publichealth.public_health_api.module.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建会话请求 DTO
 * 用于创建新的 AI 对话会话
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequestDTO {

    /**
     * 会话标题
     * 如果不提供，将使用首条消息的前20个字符
     */
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
}
