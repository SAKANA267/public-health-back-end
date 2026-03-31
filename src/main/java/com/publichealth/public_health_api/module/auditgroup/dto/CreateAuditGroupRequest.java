package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建审核组请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditGroupRequest {

    /**
     * 审核组名称
     */
    @NotBlank(message = "审核组名称不能为空")
    @Size(max = 50, message = "审核组名称不能超过50个字符")
    private String groupName;

    /**
     * 审核组编码
     */
    @NotBlank(message = "审核组编码不能为空")
    @Size(max = 20, message = "审核组编码不能超过20个字符")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "审核组编码只能包含大写字母、数字和下划线")
    private String groupCode;

    /**
     * 审核组描述
     */
    @Size(max = 200, message = "审核组描述不能超过200个字符")
    private String description;

    /**
     * 组长用户ID
     */
    private String leaderId;

    /**
     * 组状态
     */
    private AuditGroup.AuditGroupStatus status = AuditGroup.AuditGroupStatus.ACTIVE;
}
