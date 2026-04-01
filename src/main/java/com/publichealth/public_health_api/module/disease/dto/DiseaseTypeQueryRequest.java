package com.publichealth.public_health_api.module.disease.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疾病种类查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseTypeQueryRequest {

    /**
     * 页码 (从1开始)
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 搜索关键词 (疾病名称、编码或ICD编码)
     */
    private String keyword;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 状态 (1-启用 0-停用)
     */
    private Integer status;

    /**
     * 传染级别 (1-甲类 2-乙类 3-丙类 4-非传染病)
     */
    private Integer infectiousLevel;
}
