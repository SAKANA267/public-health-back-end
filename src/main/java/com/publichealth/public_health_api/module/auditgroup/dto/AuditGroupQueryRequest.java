package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核组查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditGroupQueryRequest {

    /**
     * 页码 (从1开始)
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 搜索关键词 (组名或编码)
     */
    private String keyword;

    /**
     * 组状态
     */
    private AuditGroup.AuditGroupStatus status;
}
