package com.publichealth.public_health_api.module.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 自动分配请求DTO
 */
@Data
public class AutoAssignRequest {

    /**
     * 报卡ID
     */
    @NotBlank(message = "报卡ID不能为空")
    private String reportCardId;

    /**
     * 病种分类 (用于规则匹配)
     */
    private String diseaseCategory;

    /**
     * 院区 (用于规则匹配)
     */
    private String hospitalArea;

    /**
     * 科室 (用于规则匹配)
     */
    private String department;

    /**
     * 分配人ID
     */
    @NotBlank(message = "分配人ID不能为空")
    private String assignerId;
}
