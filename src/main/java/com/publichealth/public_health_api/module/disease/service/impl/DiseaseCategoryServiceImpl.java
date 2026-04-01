package com.publichealth.public_health_api.module.disease.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.disease.dto.*;
import com.publichealth.public_health_api.module.disease.entity.DiseaseCategory;
import com.publichealth.public_health_api.module.disease.repository.DiseaseCategoryRepository;
import com.publichealth.public_health_api.module.disease.repository.DiseaseTypeRepository;
import com.publichealth.public_health_api.module.disease.service.DiseaseCategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 疾病分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiseaseCategoryServiceImpl implements DiseaseCategoryService {

    private final DiseaseCategoryRepository diseaseCategoryRepository;
    private final DiseaseTypeRepository diseaseTypeRepository;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    @Override
    @Transactional
    public DiseaseCategoryDTO createCategory(CreateDiseaseCategoryRequest request) {
        log.info("创建疾病分类: categoryName={}, categoryCode={}",
                request.getCategoryName(), request.getCategoryCode());

        // 1. 检查分类编码是否已存在
        if (diseaseCategoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw new BusinessException("疾病分类编码已存在");
        }

        // 2. 创建疾病分类实体
        DiseaseCategory category = new DiseaseCategory();
        category.setCategoryCode(request.getCategoryCode());
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        category.setRemark(request.getRemark());
        category.setDeleted(false);

        // 3. 保存疾病分类
        DiseaseCategory savedCategory = diseaseCategoryRepository.save(category);
        log.info("疾病分类创建成功: id={}, categoryName={}",
                savedCategory.getId(), savedCategory.getCategoryName());

        DiseaseCategoryDTO dto = DiseaseCategoryDTO.fromEntity(savedCategory);
        dto.setDiseaseCount(0L);

        return dto;
    }

    @Override
    public DiseaseCategoryDTO getCategoryById(String id) {
        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("疾病分类不存在");
        }

        DiseaseCategoryDTO dto = DiseaseCategoryDTO.fromEntity(category);
        dto.setDiseaseCount(diseaseTypeRepository.countByCategoryIdAndDeletedFalse(id));

        return dto;
    }

    @Override
    public DiseaseCategoryDTO getCategoryByCode(String categoryCode) {
        DiseaseCategory category = diseaseCategoryRepository.findByCategoryCodeAndDeletedFalse(categoryCode)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        DiseaseCategoryDTO dto = DiseaseCategoryDTO.fromEntity(category);
        dto.setDiseaseCount(diseaseTypeRepository.countByCategoryIdAndDeletedFalse(category.getId()));

        return dto;
    }

    @Override
    @Transactional
    public DiseaseCategoryDTO updateCategory(String id, UpdateDiseaseCategoryRequest request) {
        log.info("更新疾病分类信息: id={}", id);

        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("疾病分类不存在");
        }

        // 更新分类名称
        if (StringUtils.hasText(request.getCategoryName())) {
            category.setCategoryName(request.getCategoryName());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getDescription())) {
            category.setDescription(request.getDescription());
        }

        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        if (request.getRemark() != null) {
            category.setRemark(request.getRemark());
        }

        DiseaseCategory updatedCategory = diseaseCategoryRepository.save(category);
        log.info("疾病分类信息更新成功: id={}", id);

        return getCategoryById(id);
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        log.info("删除疾病分类: id={}", id);

        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        // 检查是否有关联的疾病
        long diseaseCount = diseaseTypeRepository.countByCategoryIdAndDeletedFalse(id);
        if (diseaseCount > 0) {
            throw new BusinessException("该分类下还有" + diseaseCount + "个疾病，无法删除");
        }

        // 逻辑删除
        category.setDeleted(true);
        diseaseCategoryRepository.save(category);

        log.info("疾病分类已删除: id={}", id);
    }

    // ============================================
    // 查询操作
    // ============================================

    @Override
    public PageResult<DiseaseCategoryDTO> getCategoryList(DiseaseCategoryQueryRequest request) {
        log.info("查询疾病分类列表: page={}, size={}, keyword={}, status={}",
                request.getPage(), request.getSize(), request.getKeyword(), request.getStatus());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.ASC, "sortOrder", "createTime")
        );

        // 统一条件查询
        Page<DiseaseCategory> page = diseaseCategoryRepository.findByConditions(
                request.getKeyword(),
                request.getStatus(),
                pageable
        );

        // 转换为DTO
        List<DiseaseCategoryDTO> dtoList = page.getContent().stream()
                .map(category -> {
                    DiseaseCategoryDTO dto = DiseaseCategoryDTO.fromEntity(category);
                    dto.setDiseaseCount(diseaseTypeRepository.countByCategoryIdAndDeletedFalse(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                dtoList
        );
    }

    @Override
    public List<DiseaseCategoryDTO> searchCategories(String keyword) {
        List<DiseaseCategory> categories = diseaseCategoryRepository.searchCategories(keyword);
        return categories.stream()
                .map(category -> {
                    DiseaseCategoryDTO dto = DiseaseCategoryDTO.fromEntity(category);
                    dto.setDiseaseCount(diseaseTypeRepository.countByCategoryIdAndDeletedFalse(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseCategoryDTO> getActiveCategories() {
        List<DiseaseCategory> categories = diseaseCategoryRepository.findByStatusAndDeletedFalse(1);
        return categories.stream()
                .map(DiseaseCategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseCategoryDTO> getActiveCategoriesSorted() {
        List<DiseaseCategory> categories = diseaseCategoryRepository.findByDeletedFalseOrderBySortOrderAsc();
        return categories.stream()
                .filter(c -> c.getStatus() == 1)
                .map(DiseaseCategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // 状态管理
    // ============================================

    @Override
    @Transactional
    public void activateCategory(String id) {
        log.info("启用疾病分类: id={}", id);

        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        category.setStatus(1);
        diseaseCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deactivateCategory(String id) {
        log.info("停用疾病分类: id={}", id);

        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病分类不存在"));

        category.setStatus(0);
        diseaseCategoryRepository.save(category);
    }

    // ============================================
    // 存在性检查
    // ============================================

    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        return diseaseCategoryRepository.existsByCategoryCodeAndDeletedFalse(categoryCode);
    }

    @Override
    public boolean existsByCategoryCodeExcludeId(String categoryCode, String excludeId) {
        return diseaseCategoryRepository.existsByCategoryCodeAndIdNot(categoryCode, excludeId);
    }
}
