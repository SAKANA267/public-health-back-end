package com.publichealth.public_health_api.module.disease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建疾病分类请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiseaseCategoryRequest {

    /**
     * 分类编码
     */
    @NotBlank(message = "分类编码不能为空")
    @Size(max = 50, message = "分类编码不能超过50个字符")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "分类编码只能包含大写字母、数字和下划线")
    private String categoryCode;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
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
    private Integer status = 1;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
