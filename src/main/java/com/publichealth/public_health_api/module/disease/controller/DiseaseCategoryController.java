package com.publichealth.public_health_api.module.disease.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.disease.dto.*;
import com.publichealth.public_health_api.module.disease.service.DiseaseCategoryService;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疾病分类控制器
 * 处理疾病分类相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/disease-categories")
@RequiredArgsConstructor
public class DiseaseCategoryController {

    private final DiseaseCategoryService diseaseCategoryService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建疾病分类
     * POST /api/disease-categories
     */
    @PostMapping
    @OperationLog(module = "疾病分类管理", operationType = OperationType.CREATE, description = "创建疾病分类")
    public ApiResponse<DiseaseCategoryDTO> createCategory(@Valid @RequestBody CreateDiseaseCategoryRequest request) {
        log.info("收到创建疾病分类请求: categoryName={}", request.getCategoryName());
        DiseaseCategoryDTO category = diseaseCategoryService.createCategory(request);
        return ApiResponse.success("疾病分类创建成功", category);
    }

    /**
     * 根据ID获取疾病分类
     * GET /api/disease-categories/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<DiseaseCategoryDTO> getCategoryById(@PathVariable String id) {
        log.info("获取疾病分类信息: id={}", id);
        DiseaseCategoryDTO category = diseaseCategoryService.getCategoryById(id);
        return ApiResponse.success(category);
    }

    /**
     * 根据分类编码获取疾病分类
     * GET /api/disease-categories/code/{categoryCode}
     */
    @GetMapping("/code/{categoryCode}")
    public ApiResponse<DiseaseCategoryDTO> getCategoryByCode(@PathVariable String categoryCode) {
        log.info("根据分类编码获取疾病分类: categoryCode={}", categoryCode);
        DiseaseCategoryDTO category = diseaseCategoryService.getCategoryByCode(categoryCode);
        return ApiResponse.success(category);
    }

    /**
     * 更新疾病分类信息
     * PUT /api/disease-categories/{id}
     */
    @PutMapping("/{id}")
    @OperationLog(module = "疾病分类管理", operationType = OperationType.UPDATE, description = "更新疾病分类信息")
    public ApiResponse<DiseaseCategoryDTO> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateDiseaseCategoryRequest request) {
        log.info("更新疾病分类信息: id={}", id);
        DiseaseCategoryDTO category = diseaseCategoryService.updateCategory(id, request);
        return ApiResponse.success("疾病分类信息更新成功", category);
    }

    /**
     * 删除疾病分类 (逻辑删除)
     * DELETE /api/disease-categories/{id}
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "疾病分类管理", operationType = OperationType.DELETE, description = "删除疾病分类")
    public ApiResponse<Void> deleteCategory(@PathVariable String id) {
        log.info("删除疾病分类: id={}", id);
        diseaseCategoryService.deleteCategory(id);
        return ApiResponse.success("疾病分类已删除");
    }

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询疾病分类列表
     * GET /api/disease-categories?page=1&size=10&keyword=xxx&status=1
     */
    @GetMapping
    public ApiResponse<PageResult<DiseaseCategoryDTO>> getCategoryList(DiseaseCategoryQueryRequest request) {
        log.info("查询疾病分类列表: {}", request);
        PageResult<DiseaseCategoryDTO> result = diseaseCategoryService.getCategoryList(request);
        return ApiResponse.success(result);
    }

    /**
     * 搜索疾病分类
     * GET /api/disease-categories/search?keyword=xxx
     */
    @GetMapping("/search")
    public ApiResponse<List<DiseaseCategoryDTO>> searchCategories(@RequestParam String keyword) {
        log.info("搜索疾病分类: keyword={}", keyword);
        List<DiseaseCategoryDTO> categories = diseaseCategoryService.searchCategories(keyword);
        return ApiResponse.success(categories);
    }

    /**
     * 获取所有启用的疾病分类
     * GET /api/disease-categories/active
     */
    @GetMapping("/active")
    public ApiResponse<List<DiseaseCategoryDTO>> getActiveCategories() {
        log.info("获取所有启用的疾病分类");
        List<DiseaseCategoryDTO> categories = diseaseCategoryService.getActiveCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 获取所有启用的疾病分类（按排序序号）
     * GET /api/disease-categories/active/sorted
     */
    @GetMapping("/active/sorted")
    public ApiResponse<List<DiseaseCategoryDTO>> getActiveCategoriesSorted() {
        log.info("获取所有启用的疾病分类（按排序序号）");
        List<DiseaseCategoryDTO> categories = diseaseCategoryService.getActiveCategoriesSorted();
        return ApiResponse.success(categories);
    }

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用疾病分类
     * PUT /api/disease-categories/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @OperationLog(module = "疾病分类管理", operationType = OperationType.UPDATE, description = "启用疾病分类")
    public ApiResponse<Void> activateCategory(@PathVariable String id) {
        log.info("启用疾病分类: id={}", id);
        diseaseCategoryService.activateCategory(id);
        return ApiResponse.success("疾病分类已启用");
    }

    /**
     * 停用疾病分类
     * PUT /api/disease-categories/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @OperationLog(module = "疾病分类管理", operationType = OperationType.UPDATE, description = "停用疾病分类")
    public ApiResponse<Void> deactivateCategory(@PathVariable String id) {
        log.info("停用疾病分类: id={}", id);
        diseaseCategoryService.deactivateCategory(id);
        return ApiResponse.success("疾病分类已停用");
    }

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查分类编码是否存在
     * GET /api/disease-categories/check/code?categoryCode=xxx&excludeId=xxx
     */
    @GetMapping("/check/code")
    public ApiResponse<Boolean> checkCategoryCodeExists(
            @RequestParam String categoryCode,
            @RequestParam(required = false) String excludeId) {
        boolean exists = excludeId != null
                ? diseaseCategoryService.existsByCategoryCodeExcludeId(categoryCode, excludeId)
                : diseaseCategoryService.existsByCategoryCode(categoryCode);
        return ApiResponse.success(exists);
    }
}
