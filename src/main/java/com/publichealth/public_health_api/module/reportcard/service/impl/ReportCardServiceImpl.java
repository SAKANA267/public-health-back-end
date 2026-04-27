package com.publichealth.public_health_api.module.reportcard.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupMemberRepository;
import com.publichealth.public_health_api.module.reportcard.dto.*;
import com.publichealth.public_health_api.module.reportcard.dto.statistics.*;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final AuditGroupMemberRepository auditGroupMemberRepository;

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
        entity.setAuditStatus(ReportCard.ReportStatus.PENDING);
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
        if (entity.getAuditStatus() != ReportCard.ReportStatus.PENDING) {
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
        log.info("查询报告卡列表: page={}, size={}, keyword={}, status={}, hospitalArea={}, department={}, startTime={}, endTime={}",
                request.getPage(), request.getSize(), request.getKeyword(), request.getStatus(),
                request.getHospitalArea(), request.getDepartment(), request.getStartTime(), request.getEndTime());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,  // Spring Data JPA 页码从0开始
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        // 统一条件查询
        Page<ReportCard> page = repository.findByConditions(
                request.getKeyword(),
                request.getStatus(),
                request.getHospitalArea(),
                request.getDepartment(),
                request.getAuditorId(),
                request.getStartTime(),
                request.getEndTime(),
                request.getIncludeDeleted(),
                pageable
        );

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
        List<ReportCard> entities = repository.findByAuditStatusAndDeletedFalse(status);
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

    @Override
    public PageResult<ReportCardDTO> getUnassignedReportCards(ReportCardQueryRequest request) {
        log.info("查询未分配报告卡列表: page={}, size={}, keyword={}, hospitalArea={}, department={}",
                request.getPage(), request.getSize(), request.getKeyword(),
                request.getHospitalArea(), request.getDepartment());

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        // 查询未分配的报告卡
        Page<ReportCard> page = repository.findByAssignStatusAndDeletedFalse(
                ReportCard.AssignStatus.UNASSIGNED,
                pageable
        );

        // 根据条件过滤 (前端传来的额外条件)
        List<ReportCardDTO> dtoList = page.getContent().stream()
                .filter(card -> {
                    boolean match = true;
                    if (StringUtils.hasText(request.getKeyword())) {
                        String keyword = request.getKeyword().toLowerCase();
                        match = match && (
                            card.getName().toLowerCase().contains(keyword) ||
                            card.getDiagnosisName().toLowerCase().contains(keyword) ||
                            card.getInpatientNo().toLowerCase().contains(keyword)
                        );
                    }
                    if (StringUtils.hasText(request.getHospitalArea())) {
                        match = match && card.getHospitalArea().equals(request.getHospitalArea());
                    }
                    if (StringUtils.hasText(request.getDepartment())) {
                        match = match && card.getDepartment().equals(request.getDepartment());
                    }
                    return match;
                })
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
    public List<ReportCardDTO> getReportCardsByAssignStatus(ReportCard.AssignStatus assignStatus) {
        List<ReportCard> entities = repository.findByAssignStatusAndDeletedFalse(assignStatus);
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
        if (entity.getAuditStatus() != ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        // 获取审核人信息
        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        // 更新审核信息
        entity.setAuditStatus(ReportCard.ReportStatus.APPROVED);
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
        if (entity.getAuditStatus() != ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        // 获取审核人信息
        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        // 更新审核信息
        entity.setAuditStatus(ReportCard.ReportStatus.REJECTED);
        entity.setAuditDate(LocalDate.now());
        entity.setAuditor(auditor.getName());
        entity.setAuditorId(auditor.getId());
        entity.setRemark(remark);

        repository.save(entity);
        log.info("报告卡审核拒绝: id={}", id);
    }

    @Override
    @Transactional
    public void withdrawAudit(String id) {
        log.info("撤回审核: id={}", id);

        ReportCard entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 业务校验: 仅允许撤回已审核或审核不通过状态的报告卡
        if (entity.getAuditStatus() == ReportCard.ReportStatus.PENDING) {
            throw new BusinessException("该报告卡为待审核状态，无需撤回");
        }

        // 重置为待审核状态
        entity.setAuditStatus(ReportCard.ReportStatus.PENDING);
        entity.setAuditDate(null);
        entity.setAuditor(null);
        entity.setAuditorId(null);
        entity.setRemark(null);

        repository.save(entity);
        log.info("报告卡审核已撤回: id={}", id);
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
    public ReportCardStatisticsDTO getStatistics() {
        log.info("获取报卡统计数据");

        ReportCardStatisticsDTO dto = new ReportCardStatisticsDTO();

        // 获取总数
        dto.setTotal(repository.countByDeletedFalse());

        // 获取各状态数量
        dto.setPending(repository.countByAuditStatusAndDeletedFalse(ReportCard.ReportStatus.PENDING));
        dto.setApproved(repository.countByAuditStatusAndDeletedFalse(ReportCard.ReportStatus.APPROVED));
        dto.setRejected(repository.countByAuditStatusAndDeletedFalse(ReportCard.ReportStatus.REJECTED));

        // 获取今日新增
        dto.setTodayNew(repository.countTodayNew());

        return dto;
    }

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
        return repository.countByAuditStatusAndDeletedFalse(status);
    }

    @Override
    public List<DistributionItemDTO> getDiseaseDistribution() {
        log.info("获取疾病种类分布统计");

        List<Object[]> results = repository.countByDiagnosisGroup();

        return results.stream()
                .map(result -> new DistributionItemDTO(
                        (String) result[0],
                        (Long) result[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DistributionItemDTO> getAreaDistribution() {
        log.info("获取院区分布统计");

        List<Object[]> results = repository.countByHospitalAreaGroup();

        return results.stream()
                .map(result -> new DistributionItemDTO(
                        (String) result[0],
                        (Long) result[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrendDataDTO> getTrendData(String period) {
        log.info("获取时间趋势数据: period={}", period);

        List<Object[]> results;
        Map<String, String> labelMapping = new HashMap<>();

        switch (period.toLowerCase()) {
            case "week":
                // 最近7天数据
                LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
                results = repository.countByLast7Days(weekStart);
                // 英文星期名转中文
                labelMapping.put("Monday", "周一");
                labelMapping.put("Tuesday", "周二");
                labelMapping.put("Wednesday", "周三");
                labelMapping.put("Thursday", "周四");
                labelMapping.put("Friday", "周五");
                labelMapping.put("Saturday", "周六");
                labelMapping.put("Sunday", "周日");
                break;
            case "month":
                // 当月每周数据
                results = repository.countByWeeksInMonth();
                break;
            case "year":
                // 当年每月数据
                results = repository.countByMonthsInYear();
                break;
            default:
                throw new BusinessException("无效的周期参数: " + period);
        }

        List<TrendDataDTO> trendList = results.stream()
                .map(result -> {
                    String label = (String) result[0];
                    // 转换星期标签
                    if (labelMapping.containsKey(label)) {
                        label = labelMapping.get(label);
                    }
                    return new TrendDataDTO(label, (Long) result[1]);
                })
                .collect(Collectors.toList());

        return trendList;
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities(Integer limit) {
        log.info("获取最近审核活动: limit={}", limit);

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 10, Sort.by(Sort.Direction.DESC, "updateTime"));
        List<ReportCard> recentCards = repository.findRecentUpdatedRecords(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return recentCards.stream()
                .map(card -> {
                    RecentActivityDTO dto = new RecentActivityDTO();
                    dto.setUser(card.getAuditor() != null ? card.getAuditor() : card.getReportDoctor());
                    dto.setTarget(card.getDiagnosisName() + "-" + card.getName());
                    dto.setTime(card.getUpdateTime().format(formatter));

                    // 根据审核状态设置操作和状态
                    switch (card.getAuditStatus()) {
                        case APPROVED:
                            dto.setAction("审核通过");
                            dto.setStatus("success");
                            break;
                        case REJECTED:
                            dto.setAction("审核驳回");
                            dto.setStatus("danger");
                            break;
                        default:
                            dto.setAction("提交报卡");
                            dto.setStatus("pending");
                            break;
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ============================================
    // 权限过滤查询
    // ============================================

    @Override
    public PageResult<ReportCardDTO> getMyAccessibleReportCards(ReportCardQueryRequest request, String userId) {
        log.info("查询我的权限组可访问报告卡列表: userId={}, page={}, size={}", userId, request.getPage(), request.getSize());

        // 获取用户所属的审核组ID列表
        List<String> groupIds = auditGroupMemberRepository.findGroupIdsByUserId(userId);

        if (groupIds.isEmpty()) {
            // 用户不属于任何审核组，返回空结果
            log.info("用户不属于任何审核组: userId={}", userId);
            return PageResult.of(request.getPage(), request.getSize(), 0L, List.of());
        }

        log.info("用户所属审核组: userId={}, groupIds={}", userId, groupIds);

        // 构建分页参数
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        // 查询分配给用户所在审核组的报告卡
        Page<ReportCard> page = repository.findByAuditGroupIdsAndConditions(
                groupIds,
                request.getKeyword(),
                request.getStatus(),
                request.getHospitalArea(),
                request.getDepartment(),
                request.getAuditorId(),
                request.getStartTime(),
                request.getEndTime(),
                pageable
        );

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
    public List<ReportCardDTO> searchMyAccessibleReportCards(String keyword, String userId) {
        log.info("搜索我的权限组可访问报告卡: userId={}, keyword={}", userId, keyword);

        // 获取用户所属的审核组ID列表
        List<String> groupIds = auditGroupMemberRepository.findGroupIdsByUserId(userId);

        if (groupIds.isEmpty()) {
            // 用户不属于任何审核组，返回空结果
            log.info("用户不属于任何审核组: userId={}", userId);
            return List.of();
        }

        // 搜索分配给用户所在审核组的报告卡
        List<ReportCard> entities = repository.searchByAuditGroupIds(groupIds, keyword);
        return entities.stream()
                .map(ReportCardDTO::fromEntity)
                .collect(Collectors.toList());
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
