package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新审核组请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAuditGroupRequest {

    /**
     * 审核组名称
     */
    @Size(max = 50, message = "审核组名称不能超过50个字符")
    private String groupName;

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
    private AuditGroup.AuditGroupStatus status;
}
