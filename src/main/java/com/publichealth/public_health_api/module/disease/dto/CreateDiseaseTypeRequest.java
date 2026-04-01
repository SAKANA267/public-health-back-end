package com.publichealth.public_health_api.module.disease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建疾病种类请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiseaseTypeRequest {

    /**
     * 疾病编码
     */
    @NotBlank(message = "疾病编码不能为空")
    @Size(max = 50, message = "疾病编码不能超过50个字符")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "疾病编码只能包含大写字母、数字和下划线")
    private String diseaseCode;

    /**
     * 疾病名称
     */
    @NotBlank(message = "疾病名称不能为空")
    @Size(max = 100, message = "疾病名称不能超过100个字符")
    private String diseaseName;

    /**
     * 所属分类ID
     */
    @NotBlank(message = "所属分类不能为空")
    private String categoryId;

    /**
     * ICD-10编码
     */
    @Size(max = 20, message = "ICD编码不能超过20个字符")
    private String icdCode;

    /**
     * 疾病描述
     */
    @Size(max = 500, message = "疾病描述不能超过500个字符")
    private String description;

    /**
     * 传染级别: 1-甲类 2-乙类 3-丙类 4-非传染病
     */
    private Integer infectiousLevel;

    /**
     * 是否需要报卡: 1-是 0-否
     */
    private Integer reportRequired = 1;

    /**
     * 报卡时限（小时）
     */
    private Integer reportDeadline;

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
