package com.publichealth.public_health_api.module.disease.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.disease.dto.*;

import java.util.List;

/**
 * 疾病种类服务接口
 */
public interface DiseaseTypeService {

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建疾病种类
     */
    DiseaseTypeDTO createDisease(CreateDiseaseTypeRequest request);

    /**
     * 根据ID获取疾病种类
     */
    DiseaseTypeDTO getDiseaseById(String id);

    /**
     * 根据疾病编码获取疾病种类
     */
    DiseaseTypeDTO getDiseaseByCode(String diseaseCode);

    /**
     * 更新疾病种类信息
     */
    DiseaseTypeDTO updateDisease(String id, UpdateDiseaseTypeRequest request);

    /**
     * 删除疾病种类 (逻辑删除)
     */
    void deleteDisease(String id);

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询疾病种类列表
     */
    PageResult<DiseaseTypeDTO> getDiseaseList(DiseaseTypeQueryRequest request);

    /**
     * 搜索疾病种类
     */
    List<DiseaseTypeDTO> searchDiseases(String keyword);

    /**
     * 获取所有启用的疾病种类
     */
    List<DiseaseTypeDTO> getActiveDiseases();

    /**
     * 根据分类ID获取疾病列表
     */
    List<DiseaseTypeDTO> getDiseasesByCategoryId(String categoryId);

    /**
     * 根据分类ID获取启用的疾病列表 (按排序序号)
     */
    List<DiseaseTypeDTO> getActiveDiseasesByCategoryIdSorted(String categoryId);

    /**
     * 根据传染级别获取疾病列表
     */
    List<DiseaseTypeDTO> getDiseasesByInfectiousLevel(Integer infectiousLevel);

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用疾病种类
     */
    void activateDisease(String id);

    /**
     * 停用疾病种类
     */
    void deactivateDisease(String id);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查疾病编码是否存在
     */
    boolean existsByDiseaseCode(String diseaseCode);

    /**
     * 检查疾病编码是否存在 (排除指定ID)
     */
    boolean existsByDiseaseCodeExcludeId(String diseaseCode, String excludeId);
}
