package com.publichealth.public_health_api.module.reportcard.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.reportcard.dto.*;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardRepository;
import com.publichealth.public_health_api.module.reportcard.service.ReportCardService;
import com.publichealth.public_health_api.module.sysuser.dto.SysUserDTO;
import com.publichealth.public_health_api.module.sysuser.service.SysUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 传染病报告卡服务实现类
 * 包含核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final ReportCardRepository repository;
    private final SysUserService sysUserService;

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    @Override
    @Transactional
    public ReportCardDTO createReportCard(CreateReportCardRequest request) {
        log.info("创建报告卡: inpatientNo={}, name={}", request.getInpatientNo(), request.getName());

        // 1. 业务校验: 检查住院号是否已存在
        if (repository.existsByInpatientNo(request.getInpatientNo())) {
            throw new BusinessException("住院号已存在: " + request.getInpatientNo());
        }

        // 2. 创建实体
        ReportCard entity = new ReportCard();
        entity.setHospitalArea(request.getHospitalArea());
        entity.setDepartment(request.getDepartment());
        entity.setDiagnosisName(request.getDiagnosisName());
        entity.setInpatientNo(request.getInpatientNo());
        entity.setOutpatientNo(request.getOutpatientNo());
        entity.setName(request.getName());
        entity.setGender(request.getGender());
        entity.setAge(request.getAge());
        entity.setPhone(request.getPhone());
        entity.setReportDoctor(request.getReportDoctor());
        entity.setFillDate(request.getFillDate());
        entity.setStatus(ReportCard.ReportStatus.PENDING);
        entity.setDeleted(false);

        // 3. 保存
        ReportCard savedEntity = repository.save(entity);
        log.info("报告卡创建成功: id={}", savedEntity.getId());

        return ReportCardDTO.fromEntity(savedEntity);
    }

    @Override
    public ReportCardDTO getReportCardById(String id) {
        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        if (entity.getDeleted()) {
            throw new BusinessException("报告卡已被删除");
        }

        return ReportCardDTO.fromEntity(entity);
    }

    @Override
    public ReportCardDTO getReportCardByInpatientNo(String inpatientNo) {
        ReportCard entity = repository.findByInpatientNoAndDeletedFalse(inpatientNo)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        return ReportCardDTO.fromEntity(entity);
    }

    @Override
    @Transactional
    public ReportCardDTO updateReportCard(String id, UpdateReportCardRequest request) {
        log.info("更新报告卡: id={}", id);

        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 业务校验: 仅允许更新待审核状态的报告卡
        if (entity.getStatus() != ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("仅允许更新待审核状态的报告卡");
        }

        // 更新字段 (只更新非空字段)
        if (StringUtils.hasText(request.getDiagnosisName())) {
            entity.setDiagnosisName(request.getDiagnosisName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            entity.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getReportDoctor())) {
            entity.setReportDoctor(request.getReportDoctor());
        }

        ReportCard updatedEntity = repository.save(entity);
        log.info("报告卡更新成功: id={}", id);

        return ReportCardDTO.fromEntity(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteReportCard(String id) {
        log.info("删除报告卡: id={}", id);

        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 逻辑删除
        entity.setDeleted(true);
        repository.save(entity);

        log.info("报告卡已删除: id={}", id);
    }

    @Override
    @Transactional
    public void batchDeleteReportCards(List<String> ids) {
        log.info("批量删除报告卡: count={}", ids.size());

        List<ReportCard> entities = repository.findAllById(ids);
        entities.forEach(entity -> entity.setDeleted(true));
        repository.saveAll(entities);

        log.info("批量删除成功: count={}", entities.size());
    }

    // ============================================
    // 查询操作
    // ============================================

    @Override
    public PageResult<ReportCardDTO> getReportCardList(ReportCardQueryRequest request) {
        log.info("查询报告卡列表: page={}, size={}", request.getPage(), request.getSize());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,  // Spring Data JPA 页码从0开始
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "fillDate", "createTime")
        );

        Page<ReportCard> page;

        // 根据条件查询
        if (StringUtils.hasText(request.getKeyword())) {
            // 关键词搜索
            page = repository.searchRecords(request.getKeyword(), pageable);
        } else if (request.getStatus() != null && request.getFillDateStart() != null && request.getFillDateEnd() != null) {
            // 状态 + 日期范围
            LocalDate start = request.getFillDateStart();
            LocalDate end = LocalDate.parse(request.getFillDateEnd()).plusDays(1);
            page = repository.findByStatusAndDateRange(request.getStatus(), start, end, pageable);
        } else if (request.getStatus() != null && StringUtils.hasText(request.getHospitalArea()) && StringUtils.hasText(request.getDepartment())) {
            // 院区 + 科室 + 状态
            page = repository.findByHospitalAreaAndDepartmentAndStatus(
                    request.getHospitalArea(), request.getDepartment(), request.getStatus(), pageable);
        } else if (request.getStatus() != null && StringUtils.hasText(request.getHospitalArea())) {
            // 院区 + 状态
            page = repository.findByHospitalAreaAndStatus(request.getHospitalArea(), request.getStatus(), pageable);
        } else if (request.getStatus() != null && StringUtils.hasText(request.getDepartment())) {
            // 科室 + 状态
            page = repository.findByDepartmentAndStatus(request.getDepartment(), request.getStatus(), pageable);
        } else if (request.getStatus() != null) {
            // 仅状态
            page = repository.findByStatusAndDeletedFalse(request.getStatus(), pageable);
        } else if (StringUtils.hasText(request.getHospitalArea()) && StringUtils.hasText(request.getDepartment())) {
            // 院区 + 科室
            page = repository.findByHospitalAreaAndDepartmentAndDeletedFalse(
                    request.getHospitalArea(), request.getDepartment(), pageable);
        } else if (StringUtils.hasText(request.getHospitalArea())) {
            // 仅院区
            page = repository.findByHospitalAreaAndDeletedFalse(request.getHospitalArea(), pageable);
        } else if (StringUtils.hasText(request.getDepartment())) {
            // 仅科室
            page = repository.findByDepartmentAndDeletedFalse(request.getDepartment(), pageable);
        } else {
            // 默认查询所有未删除的记录
            if (Boolean.TRUE.equals(request.getIncludeDeleted())) {
                page = repository.findAll(pageable);
            } else {
                page = repository.findByDeletedFalse(pageable);
            }
        }

        // 转换为DTO
        List<ReportCardDTO> dtoList = page.getContent().stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                page.getTotalElements(),
                dtoList
        );
    }

    @Override
    public List<ReportCardDTO> searchReportCards(String keyword) {
        List<ReportCard> entities = repository.searchRecordsList(keyword);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByStatus(ReportCard.ReportStatus status) {
        List<ReportCard> entities = repository.findByStatusAndDeletedFalse(status);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByHospitalArea(String hospitalArea) {
        List<ReportCard> entities = repository.findByHospitalAreaAndDeletedFalse(hospitalArea);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByDepartment(String department) {
        List<ReportCard> entities = repository.findByDepartmentAndDeletedFalse(department);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // 审核业务操作
    // ============================================

    @Override
    @Transactional
    public void approveReportCard(String id, String auditorId, String remark) {
        log.info("审核通过报告卡: id={}, auditorId={}", id, auditorId);

        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 业务校验: 仅允许审核待审核状态的报告卡
        if (entity.getStatus() != ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        // 获取审核人信息
        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        // 更新审核信息
        entity.setStatus(ReportCard.ReportStatus.APPROVED);
        entity.setAuditDate(LocalDate.now());
        entity.setAuditor(auditor.getName());
        entity.setAuditorId(auditor.getId());
        entity.setRemark(remark);

        repository.save(entity);
        log.info("报告卡审核通过: id={}", id);
    }

    @Override
    @Transactional
    public void rejectReportCard(String id, String auditorId, String remark) {
        log.info("审核拒绝报告卡: id={}, auditorId={}", id, auditorId);

        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 业务校验: 仅允许审核待审核状态的报告卡
        if (entity.getStatus() != ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        // 获取审核人信息
        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        // 更新审核信息
        entity.setStatus(ReportCard.ReportStatus.REJECTED);
        entity.setAuditDate(LocalDate.now());
        entity.setAuditor(auditor.getName());
        entity.setAuditorId(auditor.getId());
        entity.setRemark(remark);

        repository.save(entity);
        log.info("报告卡审核拒绝: id={}", id);
    }

    @Override
    public PageResult<ReportCardDTO> getPendingCards(ReportCardQueryRequest request) {
        log.info("查询待审核报告卡列表: page={}, size={}", request.getPage(), request.getSize());

        // 强制设置为待审核状态
        request.setStatus(ReportCard.ReportStatus.PENDING);

        return getReportCardList(request);
    }

    @Override
    public List<ReportCardDTO> getMyAuditedCards(String auditorId) {
        List<ReportCard> entities = repository.findByAuditorIdAndDeletedFalse(auditorId);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // 统计查询
    // ============================================

    @Override
    public Map<String, Long> getStatusStatistics() {
        List<Object[]> results = repository.countByStatus();

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("待审核", 0L);
        statistics.put("已审核", 0L);
        statistics.put("审核不通过", 0L);

        for (Object[] result : results) {
            ReportCard.ReportStatus status = (ReportCard.ReportStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status.getDescription(), count);
        }

        return statistics;
    }

    @Override
    public long getCountByStatus(ReportCard.ReportStatus status) {
        return repository.countByStatusAndDeletedFalse(status);
    }

    // ============================================
    // 存在性检查
    // ============================================

    @Override
    public boolean existsByInpatientNo(String inpatientNo) {
        return repository.existsByInpatientNo(inpatientNo);
    }

    @Override
    public boolean existsByOutpatientNo(String outpatientNo) {
        return repository.existsByOutpatientNo(outpatientNo);
    }
}
