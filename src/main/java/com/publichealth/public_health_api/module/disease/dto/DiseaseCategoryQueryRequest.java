package com.publichealth.public_health_api.module.disease.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疾病分类查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseCategoryQueryRequest {

    /**
     * 页码 (从1开始)
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 搜索关键词 (分类名称或编码)
     */
    private String keyword;

    /**
     * 状态 (1-启用 0-停用)
     */
    private Integer status;
}
