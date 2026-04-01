package com.publichealth.public_health_api.module.disease.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新疾病种类请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiseaseTypeRequest {

    /**
     * 疾病名称
     */
    @Size(max = 100, message = "疾病名称不能超过100个字符")
    private String diseaseName;

    /**
     * 所属分类ID
     */
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
    private Integer reportRequired;

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
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
