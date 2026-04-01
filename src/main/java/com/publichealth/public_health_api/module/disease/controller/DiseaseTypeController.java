package com.publichealth.public_health_api.module.disease.controller;

import com.publichealth.public_health_api.annotation.OperationLog;
import com.publichealth.public_health_api.common.ApiResponse;
import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.disease.dto.*;
import com.publichealth.public_health_api.module.disease.service.DiseaseTypeService;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疾病种类控制器
 * 处理疾病种类相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/disease-types")
@RequiredArgsConstructor
public class DiseaseTypeController {

    private final DiseaseTypeService diseaseTypeService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建疾病种类
     * POST /api/disease-types
     */
    @PostMapping
    @OperationLog(module = "疾病种类管理", operationType = OperationType.CREATE, description = "创建疾病种类")
    public ApiResponse<DiseaseTypeDTO> createDisease(@Valid @RequestBody CreateDiseaseTypeRequest request) {
        log.info("收到创建疾病种类请求: diseaseName={}", request.getDiseaseName());
        DiseaseTypeDTO disease = diseaseTypeService.createDisease(request);
        return ApiResponse.success("疾病种类创建成功", disease);
    }

    /**
     * 根据ID获取疾病种类
     * GET /api/disease-types/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<DiseaseTypeDTO> getDiseaseById(@PathVariable String id) {
        log.info("获取疾病种类信息: id={}", id);
        DiseaseTypeDTO disease = diseaseTypeService.getDiseaseById(id);
        return ApiResponse.success(disease);
    }

    /**
     * 根据疾病编码获取疾病种类
     * GET /api/disease-types/code/{diseaseCode}
     */
    @GetMapping("/code/{diseaseCode}")
    public ApiResponse<DiseaseTypeDTO> getDiseaseByCode(@PathVariable String diseaseCode) {
        log.info("根据疾病编码获取疾病种类: diseaseCode={}", diseaseCode);
        DiseaseTypeDTO disease = diseaseTypeService.getDiseaseByCode(diseaseCode);
        return ApiResponse.success(disease);
    }

    /**
     * 更新疾病种类信息
     * PUT /api/disease-types/{id}
     */
    @PutMapping("/{id}")
    @OperationLog(module = "疾病种类管理", operationType = OperationType.UPDATE, description = "更新疾病种类信息")
    public ApiResponse<DiseaseTypeDTO> updateDisease(
            @PathVariable String id,
            @Valid @RequestBody UpdateDiseaseTypeRequest request) {
        log.info("更新疾病种类信息: id={}", id);
        DiseaseTypeDTO disease = diseaseTypeService.updateDisease(id, request);
        return ApiResponse.success("疾病种类信息更新成功", disease);
    }

    /**
     * 删除疾病种类 (逻辑删除)
     * DELETE /api/disease-types/{id}
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "疾病种类管理", operationType = OperationType.DELETE, description = "删除疾病种类")
    public ApiResponse<Void> deleteDisease(@PathVariable String id) {
        log.info("删除疾病种类: id={}", id);
        diseaseTypeService.deleteDisease(id);
        return ApiResponse.success("疾病种类已删除");
    }

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询疾病种类列表
     * GET /api/disease-types?page=1&size=10&keyword=xxx&categoryId=xxx&status=1&infectiousLevel=1
     */
    @GetMapping
    public ApiResponse<PageResult<DiseaseTypeDTO>> getDiseaseList(DiseaseTypeQueryRequest request) {
        log.info("查询疾病种类列表: {}", request);
        PageResult<DiseaseTypeDTO> result = diseaseTypeService.getDiseaseList(request);
        return ApiResponse.success(result);
    }

    /**
     * 搜索疾病种类
     * GET /api/disease-types/search?keyword=xxx
     */
    @GetMapping("/search")
    public ApiResponse<List<DiseaseTypeDTO>> searchDiseases(@RequestParam String keyword) {
        log.info("搜索疾病种类: keyword={}", keyword);
        List<DiseaseTypeDTO> diseases = diseaseTypeService.searchDiseases(keyword);
        return ApiResponse.success(diseases);
    }

    /**
     * 获取所有启用的疾病种类
     * GET /api/disease-types/active
     */
    @GetMapping("/active")
    public ApiResponse<List<DiseaseTypeDTO>> getActiveDiseases() {
        log.info("获取所有启用的疾病种类");
        List<DiseaseTypeDTO> diseases = diseaseTypeService.getActiveDiseases();
        return ApiResponse.success(diseases);
    }

    /**
     * 根据分类ID获取疾病列表
     * GET /api/disease-types/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<DiseaseTypeDTO>> getDiseasesByCategoryId(@PathVariable String categoryId) {
        log.info("根据分类ID获取疾病列表: categoryId={}", categoryId);
        List<DiseaseTypeDTO> diseases = diseaseTypeService.getDiseasesByCategoryId(categoryId);
        return ApiResponse.success(diseases);
    }

    /**
     * 根据分类ID获取启用的疾病列表（按排序序号）
     * GET /api/disease-types/category/{categoryId}/active/sorted
     */
    @GetMapping("/category/{categoryId}/active/sorted")
    public ApiResponse<List<DiseaseTypeDTO>> getActiveDiseasesByCategoryIdSorted(@PathVariable String categoryId) {
        log.info("根据分类ID获取启用的疾病列表（按排序序号）: categoryId={}", categoryId);
        List<DiseaseTypeDTO> diseases = diseaseTypeService.getActiveDiseasesByCategoryIdSorted(categoryId);
        return ApiResponse.success(diseases);
    }

    /**
     * 根据传染级别获取疾病列表
     * GET /api/disease-types/infectious/{level}
     */
    @GetMapping("/infectious/{level}")
    public ApiResponse<List<DiseaseTypeDTO>> getDiseasesByInfectiousLevel(@PathVariable Integer level) {
        log.info("根据传染级别获取疾病列表: level={}", level);
        List<DiseaseTypeDTO> diseases = diseaseTypeService.getDiseasesByInfectiousLevel(level);
        return ApiResponse.success(diseases);
    }

    // ============================================
    // 状态管理
    // ============================================

    /**
     * 启用疾病种类
     * PUT /api/disease-types/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @OperationLog(module = "疾病种类管理", operationType = OperationType.UPDATE, description = "启用疾病种类")
    public ApiResponse<Void> activateDisease(@PathVariable String id) {
        log.info("启用疾病种类: id={}", id);
        diseaseTypeService.activateDisease(id);
        return ApiResponse.success("疾病种类已启用");
    }

    /**
     * 停用疾病种类
     * PUT /api/disease-types/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @OperationLog(module = "疾病种类管理", operationType = OperationType.UPDATE, description = "停用疾病种类")
    public ApiResponse<Void> deactivateDisease(@PathVariable String id) {
        log.info("停用疾病种类: id={}", id);
        diseaseTypeService.deactivateDisease(id);
        return ApiResponse.success("疾病种类已停用");
    }

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查疾病编码是否存在
     * GET /api/disease-types/check/code?diseaseCode=xxx&excludeId=xxx
     */
    @GetMapping("/check/code")
    public ApiResponse<Boolean> checkDiseaseCodeExists(
            @RequestParam String diseaseCode,
            @RequestParam(required = false) String excludeId) {
        boolean exists = excludeId != null
                ? diseaseTypeService.existsByDiseaseCodeExcludeId(diseaseCode, excludeId)
                : diseaseTypeService.existsByDiseaseCode(diseaseCode);
        return ApiResponse.success(exists);
    }
}
