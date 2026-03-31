package com.publichealth.public_health_api.module.auditgroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 移除组成员请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveMemberRequest {

    /**
     * 审核组ID
     */
    @NotBlank(message = "审核组ID不能为空")
    private String groupId;

    /**
     * 用户ID列表
     */
    @NotEmpty(message = "用户ID列表不能为空")
    private List<String> userIds;
}
