package com.publichealth.public_health_api.module.disease.dto;

import com.publichealth.public_health_api.module.disease.entity.DiseaseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 疾病分类数据传输对象
 * 用于返回给前端的疾病分类信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseCategoryDTO {

    private String id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private String statusText;
    private Long diseaseCount;  // 该分类下的疾病数量
    private String createdBy;
    private LocalDateTime createTime;
    private String updatedBy;
    private LocalDateTime updateTime;
    private String remark;

    /**
     * 从实体转换为DTO
     */
    public static DiseaseCategoryDTO fromEntity(DiseaseCategory category) {
        if (category == null) {
            return null;
        }
        DiseaseCategoryDTO dto = new DiseaseCategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryCode(category.getCategoryCode());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        dto.setStatusText(DiseaseCategory.Status.fromValue(category.getStatus()).getDescription());
        dto.setCreatedBy(category.getCreatedBy());
        dto.setCreateTime(category.getCreateTime());
        dto.setUpdatedBy(category.getUpdatedBy());
        dto.setUpdateTime(category.getUpdateTime());
        dto.setRemark(category.getRemark());
        return dto;
    }
}
