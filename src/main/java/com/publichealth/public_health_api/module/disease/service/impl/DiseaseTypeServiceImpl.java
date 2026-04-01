package com.publichealth.public_health_api.module.disease.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.disease.dto.*;
import com.publichealth.public_health_api.module.disease.entity.DiseaseCategory;
import com.publichealth.public_health_api.module.disease.entity.DiseaseType;
import com.publichealth.public_health_api.module.disease.repository.DiseaseCategoryRepository;
import com.publichealth.public_health_api.module.disease.repository.DiseaseTypeRepository;
import com.publichealth.public_health_api.module.disease.service.DiseaseTypeService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 疾病种类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiseaseTypeServiceImpl implements DiseaseTypeService {

    private final DiseaseTypeRepository diseaseTypeRepository;
    private final DiseaseCategoryRepository diseaseCategoryRepository;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    @Override
    @Transactional
    public DiseaseTypeDTO createDisease(CreateDiseaseTypeRequest request) {
        log.info("创建疾病种类: diseaseName={}, diseaseCode={}",
                request.getDiseaseName(), request.getDiseaseCode());

        // 1. 检查疾病编码是否已存在
        if (diseaseTypeRepository.existsByDiseaseCode(request.getDiseaseCode())) {
            throw new BusinessException("疾病编码已存在");
        }

        // 2. 验证分类是否存在
        DiseaseCategory category = diseaseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("指定的疾病分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("指定的疾病分类不存在");
        }

        // 3. 创建疾病种类实体
        DiseaseType disease = new DiseaseType();
        disease.setDiseaseCode(request.getDiseaseCode());
        disease.setDiseaseName(request.getDiseaseName());
        disease.setCategoryId(request.getCategoryId());
        disease.setIcdCode(request.getIcdCode());
        disease.setDescription(request.getDescription());
        disease.setInfectiousLevel(request.getInfectiousLevel());
        disease.setReportRequired(request.getReportRequired() != null ? request.getReportRequired() : 1);
        disease.setReportDeadline(request.getReportDeadline());
        disease.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        disease.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        disease.setRemark(request.getRemark());
        disease.setDeleted(false);

        // 4. 保存疾病种类
        DiseaseType savedDisease = diseaseTypeRepository.save(disease);
        log.info("疾病种类创建成功: id={}, diseaseName={}",
                savedDisease.getId(), savedDisease.getDiseaseName());

        DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(savedDisease);
        dto.setCategoryName(category.getCategoryName());

        return dto;
    }

    @Override
    public DiseaseTypeDTO getDiseaseById(String id) {
        DiseaseType disease = diseaseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        if (disease.getDeleted()) {
            throw new BusinessException("疾病种类不存在");
        }

        DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);

        // 设置分类名称
        diseaseCategoryRepository.findById(disease.getCategoryId()).ifPresent(category -> {
            dto.setCategoryName(category.getCategoryName());
        });

        return dto;
    }

    @Override
    public DiseaseTypeDTO getDiseaseByCode(String diseaseCode) {
        DiseaseType disease = diseaseTypeRepository.findByDiseaseCodeAndDeletedFalse(diseaseCode)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);

        // 设置分类名称
        diseaseCategoryRepository.findById(disease.getCategoryId()).ifPresent(category -> {
            dto.setCategoryName(category.getCategoryName());
        });

        return dto;
    }

    @Override
    @Transactional
    public DiseaseTypeDTO updateDisease(String id, UpdateDiseaseTypeRequest request) {
        log.info("更新疾病种类信息: id={}", id);

        DiseaseType disease = diseaseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        if (disease.getDeleted()) {
            throw new BusinessException("疾病种类不存在");
        }

        // 更新分类时验证分类是否存在
        if (StringUtils.hasText(request.getCategoryId()) &&
                !request.getCategoryId().equals(disease.getCategoryId())) {
            DiseaseCategory category = diseaseCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException("指定的疾病分类不存在"));
            if (category.getDeleted()) {
                throw new BusinessException("指定的疾病分类不存在");
            }
            disease.setCategoryId(request.getCategoryId());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getDiseaseName())) {
            disease.setDiseaseName(request.getDiseaseName());
        }

        if (request.getIcdCode() != null) {
            disease.setIcdCode(request.getIcdCode());
        }

        if (StringUtils.hasText(request.getDescription())) {
            disease.setDescription(request.getDescription());
        }

        if (request.getInfectiousLevel() != null) {
            disease.setInfectiousLevel(request.getInfectiousLevel());
        }

        if (request.getReportRequired() != null) {
            disease.setReportRequired(request.getReportRequired());
        }

        if (request.getReportDeadline() != null) {
            disease.setReportDeadline(request.getReportDeadline());
        }

        if (request.getSortOrder() != null) {
            disease.setSortOrder(request.getSortOrder());
        }

        if (request.getStatus() != null) {
            disease.setStatus(request.getStatus());
        }

        if (request.getRemark() != null) {
            disease.setRemark(request.getRemark());
        }

        DiseaseType updatedDisease = diseaseTypeRepository.save(disease);
        log.info("疾病种类信息更新成功: id={}", id);

        return getDiseaseById(id);
    }

    @Override
    @Transactional
    public void deleteDisease(String id) {
        log.info("删除疾病种类: id={}", id);

        DiseaseType disease = diseaseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        // 逻辑删除
        disease.setDeleted(true);
        diseaseTypeRepository.save(disease);

        log.info("疾病种类已删除: id={}", id);
    }

    // ============================================
    // 查询操作
    // ============================================

    @Override
    public PageResult<DiseaseTypeDTO> getDiseaseList(DiseaseTypeQueryRequest request) {
        log.info("查询疾病种类列表: page={}, size={}, keyword={}, categoryId={}, status={}, infectiousLevel={}",
                request.getPage(), request.getSize(), request.getKeyword(),
                request.getCategoryId(), request.getStatus(), request.getInfectiousLevel());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.ASC, "categoryId", "sortOrder", "createTime")
        );

        // 统一条件查询
        Page<DiseaseType> page = diseaseTypeRepository.findByConditions(
                request.getKeyword(),
                request.getCategoryId(),
                request.getStatus(),
                request.getInfectiousLevel(),
                pageable
        );

        // 获取所有分类ID映射到分类名称
        List<String> categoryIds = page.getContent().stream()
                .map(DiseaseType::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> categoryNameMap = diseaseCategoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(DiseaseCategory::getId, DiseaseCategory::getCategoryName));

        // 转换为DTO
        List<DiseaseTypeDTO> dtoList = page.getContent().stream()
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryNameMap.get(disease.getCategoryId()));
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
    public List<DiseaseTypeDTO> searchDiseases(String keyword) {
        List<DiseaseType> diseases = diseaseTypeRepository.searchDiseases(keyword);

        // 获取所有分类ID映射到分类名称
        List<String> categoryIds = diseases.stream()
                .map(DiseaseType::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> categoryNameMap = diseaseCategoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(DiseaseCategory::getId, DiseaseCategory::getCategoryName));

        return diseases.stream()
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryNameMap.get(disease.getCategoryId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseTypeDTO> getActiveDiseases() {
        List<DiseaseType> diseases = diseaseTypeRepository.findByStatusAndDeletedFalse(1);

        // 获取所有分类ID映射到分类名称
        List<String> categoryIds = diseases.stream()
                .map(DiseaseType::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> categoryNameMap = diseaseCategoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(DiseaseCategory::getId, DiseaseCategory::getCategoryName));

        return diseases.stream()
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryNameMap.get(disease.getCategoryId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseTypeDTO> getDiseasesByCategoryId(String categoryId) {
        List<DiseaseType> diseases = diseaseTypeRepository.findByCategoryIdAndDeletedFalse(categoryId);

        DiseaseCategory category = diseaseCategoryRepository.findById(categoryId).orElse(null);
        String categoryName = category != null ? category.getCategoryName() : "";

        return diseases.stream()
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseTypeDTO> getActiveDiseasesByCategoryIdSorted(String categoryId) {
        List<DiseaseType> diseases = diseaseTypeRepository.findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(categoryId);

        DiseaseCategory category = diseaseCategoryRepository.findById(categoryId).orElse(null);
        String categoryName = category != null ? category.getCategoryName() : "";

        return diseases.stream()
                .filter(d -> d.getStatus() == 1)
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseTypeDTO> getDiseasesByInfectiousLevel(Integer infectiousLevel) {
        List<DiseaseType> diseases = diseaseTypeRepository.findByInfectiousLevelAndDeletedFalse(infectiousLevel);

        // 获取所有分类ID映射到分类名称
        List<String> categoryIds = diseases.stream()
                .map(DiseaseType::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> categoryNameMap = diseaseCategoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(DiseaseCategory::getId, DiseaseCategory::getCategoryName));

        return diseases.stream()
                .map(disease -> {
                    DiseaseTypeDTO dto = DiseaseTypeDTO.fromEntity(disease);
                    dto.setCategoryName(categoryNameMap.get(disease.getCategoryId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ============================================
    // 状态管理
    // ============================================

    @Override
    @Transactional
    public void activateDisease(String id) {
        log.info("启用疾病种类: id={}", id);

        DiseaseType disease = diseaseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        disease.setStatus(1);
        diseaseTypeRepository.save(disease);
    }

    @Override
    @Transactional
    public void deactivateDisease(String id) {
        log.info("停用疾病种类: id={}", id);

        DiseaseType disease = diseaseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("疾病种类不存在"));

        disease.setStatus(0);
        diseaseTypeRepository.save(disease);
    }

    // ============================================
    // 存在性检查
    // ============================================

    @Override
    public boolean existsByDiseaseCode(String diseaseCode) {
        return diseaseTypeRepository.existsByDiseaseCodeAndDeletedFalse(diseaseCode);
    }

    @Override
    public boolean existsByDiseaseCodeExcludeId(String diseaseCode, String excludeId) {
        return diseaseTypeRepository.existsByDiseaseCodeAndIdNot(diseaseCode, excludeId);
    }
}
