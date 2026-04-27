package com.publichealth.public_health_api.module.reportcard.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布数据项DTO
 * 用于疾病种类分布、院区分布等统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionItemDTO {

    /**
     * 标签名称（如疾病名称、院区名称）
     */
    private String label;

    /**
     * 该类别的数量
     */
    private Long value;
}
