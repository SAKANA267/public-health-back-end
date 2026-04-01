package com.publichealth.public_health_api.module.disease.dto;

import com.publichealth.public_health_api.module.disease.entity.DiseaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 疾病种类数据传输对象
 * 用于返回给前端的疾病种类信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseTypeDTO {

    private String id;
    private String diseaseCode;
    private String diseaseName;
    private String categoryId;
    private String categoryName;  // 关联的分类名称
    private String icdCode;
    private String description;
    private Integer infectiousLevel;
    private String infectiousLevelText;
    private Integer reportRequired;
    private Integer reportDeadline;
    private Integer sortOrder;
    private Integer status;
    private String statusText;
    private String createdBy;
    private LocalDateTime createTime;
    private String updatedBy;
    private LocalDateTime updateTime;
    private String remark;

    /**
     * 从实体转换为DTO
     */
    public static DiseaseTypeDTO fromEntity(DiseaseType disease) {
        if (disease == null) {
            return null;
        }
        DiseaseTypeDTO dto = new DiseaseTypeDTO();
        dto.setId(disease.getId());
        dto.setDiseaseCode(disease.getDiseaseCode());
        dto.setDiseaseName(disease.getDiseaseName());
        dto.setCategoryId(disease.getCategoryId());
        dto.setIcdCode(disease.getIcdCode());
        dto.setDescription(disease.getDescription());
        dto.setInfectiousLevel(disease.getInfectiousLevel());
        if (disease.getInfectiousLevel() != null) {
            dto.setInfectiousLevelText(DiseaseType.InfectiousLevel.fromValue(disease.getInfectiousLevel()).getDescription());
        }
        dto.setReportRequired(disease.getReportRequired());
        dto.setReportDeadline(disease.getReportDeadline());
        dto.setSortOrder(disease.getSortOrder());
        dto.setStatus(disease.getStatus());
        dto.setStatusText(DiseaseType.Status.fromValue(disease.getStatus()).getDescription());
        dto.setCreatedBy(disease.getCreatedBy());
        dto.setCreateTime(disease.getCreateTime());
        dto.setUpdatedBy(disease.getUpdatedBy());
        dto.setUpdateTime(disease.getUpdateTime());
        dto.setRemark(disease.getRemark());
        return dto;
    }
}
