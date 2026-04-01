package com.publichealth.public_health_api.module.disease.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.disease.dto.*;

import java.util.List;

/**
 * 疾病分类服务接口
 */
public interface DiseaseCategoryService {

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建疾病分类
     */
    DiseaseCategoryDTO createCategory(CreateDiseaseCategoryRequest request);

    /**
     * 根据ID获取疾病分类
     */
    DiseaseCategoryDTO getCategoryById(String id);

    /**
     * 根据分类编码获取疾病分类
     */
    DiseaseCategoryDTO getCategoryByCode(String categoryCode);

    /**
     * 更新疾病分类信息
     */
    DiseaseCategoryDTO updateCategory(String id, UpdateDiseaseCategoryRequest request);

    /**
     * 删除疾病分类 (逻辑删除)
     */
    void deleteCategory(String id);

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询疾病分类列表
     */
    PageResult<DiseaseCategoryDTO> getCategoryList(DiseaseCategoryQueryRequest request);

    /**
     * 搜索疾病分类
     */
    List<DiseaseCategoryDTO> searchCategories(String keyword);

    /**
     * 获取所有启用的疾病分类
     */
    List<DiseaseCategoryDTO> getActiveCategories();

    /**
     * 获取所有启用的疾病分类 (按排序序号)
     */
    List<DiseaseCategoryDTO> getActiveCategoriesSorted();

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用疾病分类
     */
    void activateCategory(String id);

    /**
     * 停用疾病分类
     */
    void deactivateCategory(String id);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查分类编码是否存在
     */
    boolean existsByCategoryCode(String categoryCode);

    /**
     * 检查分类编码是否存在 (排除指定ID)
     */
    boolean existsByCategoryCodeExcludeId(String categoryCode, String excludeId);
}
