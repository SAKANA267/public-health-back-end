package com.publichealth.public_health_api.module.disease.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新疾病分类请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiseaseCategoryRequest {

    /**
     * 分类名称
     */
    @Size(max = 100, message = "分类名称不能超过100个字符")
    private String categoryName;

    /**
     * 分类描述
     */
    @Size(max = 500, message = "分类描述不能超过500个字符")
    private String description;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
