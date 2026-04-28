package com.publichealth.public_health_api.module.reportcard.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.auditgroup.repository.AuditGroupMemberRepository;
import com.publichealth.public_health_api.module.reportcard.dto.*;
import com.publichealth.public_health_api.module.reportcard.dto.statistics.*;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardDiagnosis;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardAuditRepository;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardDiagnosisRepository;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardPatientRepository;
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
 * 迁移说明：支持4张表的关联操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final ReportCardRepository repository;
    private final ReportCardPatientRepository patientRepository;
    private final ReportCardDiagnosisRepository diagnosisRepository;
    private final ReportCardAuditRepository auditRepository;
    private final SysUserService sysUserService;
    private final AuditGroupMemberRepository auditGroupMemberRepository;

    @Override
    @Transactional
    public ReportCardDTO createReportCard(CreateReportCardRequest request) {
        log.info("创建报告卡: inpatientNo={}, name={}",
                request.getInpatientNo(), request.getPatientInfo().getPatientName());

        if (StringUtils.hasText(request.getInpatientNo()) &&
                repository.existsByInpatientNo(request.getInpatientNo())) {
            throw new BusinessException("住院号已存在: " + request.getInpatientNo());
        }

        ReportCard reportCard = new ReportCard();
        reportCard.setHospitalArea(request.getHospitalArea());
        reportCard.setDepartment(request.getDepartment());
        reportCard.setInpatientNo(request.getInpatientNo());
        reportCard.setOutpatientNo(request.getOutpatientNo());
        reportCard.setDoctorName(request.getDoctorName());
        reportCard.setFillDate(request.getFillDate());
        reportCard.setCardNumber(request.getCardNumber());
        reportCard.setReportCategory(request.getReportCategory());
        reportCard.setAuditStatus(ReportCard.AuditStatus.PENDING);
        reportCard.setDeleted(false);

        CreateReportCardRequest.PatientInfo patientReq = request.getPatientInfo();
        reportCard.setPatientName(patientReq.getPatientName());

        CreateReportCardRequest.DiagnosisInfo diagnosisReq = request.getDiagnosisInfo();
        reportCard.setDiseaseName(diagnosisReq.getDiseaseName());

        reportCard = repository.save(reportCard);

        ReportCardPatient patient = new ReportCardPatient();
        patient.setReportCardId(reportCard.getId());
        patient.setPatientName(patientReq.getPatientName());
        patient.setIdCard(patientReq.getIdCard());
        patient.setBirthday(patientReq.getBirthday());
        patient.setGender(patientReq.getGender());
        patient.setAge(patientReq.getAge());
        patient.setPhone(patientReq.getPhone());
        patient.setParentName(patientReq.getParentName());
        patient.setWorkUnit(patientReq.getWorkUnit());
        patient.setAddressType(patientReq.getAddressType());
        patient.setDetailAddress(patientReq.getDetailAddress());
        patientRepository.save(patient);

        ReportCardDiagnosis diagnosis = new ReportCardDiagnosis();
        diagnosis.setReportCardId(reportCard.getId());
        diagnosis.setDiseaseName(diagnosisReq.getDiseaseName());
        diagnosis.setDiagnosisCode(diagnosisReq.getDiagnosisCode());
        diagnosis.setPatientBelong(diagnosisReq.getPatientBelong());
        diagnosis.setCrowdCategories(diagnosisReq.getCrowdCategories());
        diagnosis.setCaseType(diagnosisReq.getCaseType());
        diagnosis.setCaseAttribute(diagnosisReq.getCaseAttribute());
        diagnosis.setOnsetDate(diagnosisReq.getOnsetDate());
        diagnosis.setDiagnosisDate(diagnosisReq.getDiagnosisDate());
        diagnosis.setDeathDate(diagnosisReq.getDeathDate());
        diagnosis.setRemark(diagnosisReq.getRemark());
        diagnosisRepository.save(diagnosis);

        ReportCardAudit audit = new ReportCardAudit();
        audit.setReportCardId(reportCard.getId());
        auditRepository.save(audit);

        log.info("报告卡创建成功: id={}", reportCard.getId());
        return ReportCardDTO.fromEntity(reportCard, patient, diagnosis, audit);
    }

    @Override
    public ReportCardDTO getReportCardById(String id) {
        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        if (reportCard.getDeleted()) {
            throw new BusinessException("报告卡已被删除");
        }

        ReportCardPatient patient = patientRepository.findByReportCardId(id).orElse(null);
        ReportCardDiagnosis diagnosis = diagnosisRepository.findByReportCardId(id).orElse(null);
        ReportCardAudit audit = auditRepository.findByReportCardId(id).orElse(null);

        return ReportCardDTO.fromEntity(reportCard, patient, diagnosis, audit);
    }

    @Override
    public ReportCardDTO getReportCardByInpatientNo(String inpatientNo) {
        ReportCard reportCard = repository.findByInpatientNoAndDeletedFalse(inpatientNo)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        ReportCardPatient patient = patientRepository.findByReportCardId(reportCard.getId()).orElse(null);
        ReportCardDiagnosis diagnosis = diagnosisRepository.findByReportCardId(reportCard.getId()).orElse(null);
        ReportCardAudit audit = auditRepository.findByReportCardId(reportCard.getId()).orElse(null);

        return ReportCardDTO.fromEntity(reportCard, patient, diagnosis, audit);
    }

    @Override
    @Transactional
    public ReportCardDTO updateReportCard(String id, UpdateReportCardRequest request) {
        log.info("更新报告卡: id={}", id);

        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        ReportCardAudit audit = auditRepository.findByReportCardId(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));

        if (audit.getAuditStatus() != ReportCardAudit.AuditStatus.PENDING) {
            throw new BusinessException("仅允许更新待审核状态的报告卡");
        }

        if (StringUtils.hasText(request.getCardNumber())) {
            reportCard.setCardNumber(request.getCardNumber());
        }
        if (request.getReportCategory() != null) {
            reportCard.setReportCategory(request.getReportCategory());
        }
        if (request.getReportStatus() != null) {
            reportCard.setReportStatus(request.getReportStatus());
        }
        if (StringUtils.hasText(request.getDoctorName())) {
            reportCard.setDoctorName(request.getDoctorName());
        }

        if (request.getPatientInfo() != null) {
            ReportCardPatient patient = patientRepository.findByReportCardId(id)
                    .orElseThrow(() -> new BusinessException("患者信息不存在"));
            UpdateReportCardRequest.PatientInfoUpdate patientReq = request.getPatientInfo();

            if (StringUtils.hasText(patientReq.getPhone())) {
                patient.setPhone(patientReq.getPhone());
            }
            if (patientReq.getBirthday() != null) {
                patient.setBirthday(patientReq.getBirthday());
            }
            if (patientReq.getGender() != null) {
                patient.setGender(patientReq.getGender());
            }
            if (patientReq.getAge() != null) {
                patient.setAge(patientReq.getAge());
            }
            if (StringUtils.hasText(patientReq.getParentName())) {
                patient.setParentName(patientReq.getParentName());
            }
            if (StringUtils.hasText(patientReq.getWorkUnit())) {
                patient.setWorkUnit(patientReq.getWorkUnit());
            }
            if (patientReq.getAddressType() != null) {
                patient.setAddressType(patientReq.getAddressType());
            }
            if (StringUtils.hasText(patientReq.getDetailAddress())) {
                patient.setDetailAddress(patientReq.getDetailAddress());
            }
            patientRepository.save(patient);
        }

        if (request.getDiagnosisInfo() != null) {
            ReportCardDiagnosis diagnosis = diagnosisRepository.findByReportCardId(id)
                    .orElseThrow(() -> new BusinessException("诊断信息不存在"));
            UpdateReportCardRequest.DiagnosisInfoUpdate diagnosisReq = request.getDiagnosisInfo();

            if (StringUtils.hasText(diagnosisReq.getDiseaseName())) {
                diagnosis.setDiseaseName(diagnosisReq.getDiseaseName());
                reportCard.setDiseaseName(diagnosisReq.getDiseaseName());
            }
            if (StringUtils.hasText(diagnosisReq.getDiagnosisCode())) {
                diagnosis.setDiagnosisCode(diagnosisReq.getDiagnosisCode());
            }
            if (diagnosisReq.getPatientBelong() != null) {
                diagnosis.setPatientBelong(diagnosisReq.getPatientBelong());
            }
            if (diagnosisReq.getCrowdCategories() != null) {
                diagnosis.setCrowdCategories(diagnosisReq.getCrowdCategories());
            }
            if (diagnosisReq.getCaseType() != null) {
                diagnosis.setCaseType(diagnosisReq.getCaseType());
            }
            if (diagnosisReq.getCaseAttribute() != null) {
                diagnosis.setCaseAttribute(diagnosisReq.getCaseAttribute());
            }
            if (diagnosisReq.getOnsetDate() != null) {
                diagnosis.setOnsetDate(diagnosisReq.getOnsetDate());
            }
            if (diagnosisReq.getDiagnosisDate() != null) {
                diagnosis.setDiagnosisDate(diagnosisReq.getDiagnosisDate());
            }
            if (diagnosisReq.getDeathDate() != null) {
                diagnosis.setDeathDate(diagnosisReq.getDeathDate());
            }
            if (StringUtils.hasText(diagnosisReq.getRemark())) {
                diagnosis.setRemark(diagnosisReq.getRemark());
            }
            diagnosisRepository.save(diagnosis);
        }

        ReportCard updated = repository.save(reportCard);
        log.info("报告卡更新成功: id={}", id);

        ReportCardPatient patientResult = patientRepository.findByReportCardId(id).orElse(null);
        ReportCardDiagnosis diagnosisResult = diagnosisRepository.findByReportCardId(id).orElse(null);
        ReportCardAudit auditResult = auditRepository.findByReportCardId(id).orElse(null);

        return ReportCardDTO.fromEntity(updated, patientResult, diagnosisResult, auditResult);
    }

    @Override
    @Transactional
    public void deleteReportCard(String id) {
        log.info("删除报告卡: id={}", id);

        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        reportCard.setDeleted(true);
        repository.save(reportCard);

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

    @Override
    public PageResult<ReportCardDTO> getReportCardList(ReportCardQueryRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

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

        List<ReportCardDTO> dtoList = page.getContent().stream()
                .map(ReportCardDTO::forList)
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
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByStatus(ReportCard.AuditStatus status) {
        List<ReportCard> entities = repository.findByAuditStatusAndDeletedFalse(status);
        return entities.stream()
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByHospitalArea(String hospitalArea) {
        List<ReportCard> entities = repository.findByHospitalAreaAndDeletedFalse(hospitalArea);
        return entities.stream()
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByDepartment(String department) {
        List<ReportCard> entities = repository.findByDepartmentAndDeletedFalse(department);
        return entities.stream()
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ReportCardDTO> getUnassignedReportCards(ReportCardQueryRequest request) {
        log.info("查询未分配报告卡列表: page={}, size={}", request.getPage(), request.getSize());

        List<ReportCardAudit> unassignedAudits = auditRepository.findByAuditStatusAndAssignStatus(
                ReportCardAudit.AuditStatus.PENDING,
                ReportCardAudit.AssignStatus.UNASSIGNED
        );

        List<String> reportCardIds = unassignedAudits.stream()
                .map(ReportCardAudit::getReportCardId)
                .toList();

        List<ReportCard> allCards = repository.findAllById(reportCardIds).stream()
                .filter(card -> !card.getDeleted())
                .collect(Collectors.toList());

        List<ReportCardDTO> filteredCards = allCards.stream()
                .filter(card -> {
                    boolean match = true;
                    if (StringUtils.hasText(request.getKeyword())) {
                        String keyword = request.getKeyword().toLowerCase();
                        match = match && (
                                card.getPatientName().toLowerCase().contains(keyword) ||
                                        card.getDiseaseName().toLowerCase().contains(keyword) ||
                                        (card.getInpatientNo() != null && card.getInpatientNo().toLowerCase().contains(keyword))
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
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());

        int start = (request.getPage() - 1) * request.getSize();
        int end = Math.min(start + request.getSize(), filteredCards.size());
        List<ReportCardDTO> pageContent = start < filteredCards.size()
                ? filteredCards.subList(start, end)
                : List.of();

        return PageResult.of(
                request.getPage(),
                request.getSize(),
                (long) filteredCards.size(),
                pageContent
        );
    }

    @Override
    public List<ReportCardDTO> getReportCardsByAssignStatus(ReportCardAudit.AssignStatus assignStatus) {
        List<ReportCardAudit> audits = auditRepository.findByAuditStatusAndAssignStatus(
                ReportCardAudit.AuditStatus.PENDING,
                assignStatus
        );

        return audits.stream()
                .map(audit -> {
                    ReportCard reportCard = repository.findById(audit.getReportCardId()).orElse(null);
                    if (reportCard != null && !reportCard.getDeleted()) {
                        return ReportCardDTO.forList(reportCard);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ReportCardDTO> getMyAccessibleReportCards(ReportCardQueryRequest request, String userId) {
        log.info("查询我的权限组可访问报告卡列表: userId={}, page={}, size={}",
                userId, request.getPage(), request.getSize());

        List<String> groupIds = auditGroupMemberRepository.findGroupIdsByUserId(userId);

        if (groupIds.isEmpty()) {
            log.info("用户不属于任何审核组: userId={}", userId);
            return PageResult.of(request.getPage(), request.getSize(), 0L, List.of());
        }

        log.info("用户所属审核组: userId={}, groupIds={}", userId, groupIds);

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

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

        List<ReportCardDTO> dtoList = page.getContent().stream()
                .map(ReportCardDTO::forList)
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

        List<String> groupIds = auditGroupMemberRepository.findGroupIdsByUserId(userId);

        if (groupIds.isEmpty()) {
            log.info("用户不属于任何审核组: userId={}", userId);
            return List.of();
        }

        List<ReportCard> entities = repository.searchByAuditGroupIds(groupIds, keyword);
        return entities.stream()
                .map(ReportCardDTO::forList)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveReportCard(String id, String auditorId, String remark) {
        log.info("审核通过报告卡: id={}, auditorId={}", id, auditorId);

        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        ReportCardAudit audit = auditRepository.findByReportCardId(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));

        if (audit.getAuditStatus() != ReportCardAudit.AuditStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        audit.setAuditStatus(ReportCardAudit.AuditStatus.APPROVED);
        audit.setAuditorId(auditorId);
        audit.setAuditorName(auditor.getName());
        audit.setAuditDate(LocalDateTime.now());
        audit.setRejectReason(remark);
        auditRepository.save(audit);

        reportCard.setAuditStatus(ReportCard.AuditStatus.APPROVED);
        repository.save(reportCard);

        log.info("报告卡审核通过: id={}", id);
    }

    @Override
    @Transactional
    public void rejectReportCard(String id, String auditorId, String remark) {
        log.info("审核拒绝报告卡: id={}, auditorId={}", id, auditorId);

        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        ReportCardAudit audit = auditRepository.findByReportCardId(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));

        if (audit.getAuditStatus() != ReportCardAudit.AuditStatus.PENDING) {
            throw new BusinessException("仅允许审核待审核状态的报告卡");
        }

        SysUserDTO auditor = sysUserService.getUserById(auditorId);

        audit.setAuditStatus(ReportCardAudit.AuditStatus.REJECTED);
        audit.setAuditorId(auditorId);
        audit.setAuditorName(auditor.getName());
        audit.setAuditDate(LocalDateTime.now());
        audit.setRejectReason(remark);
        auditRepository.save(audit);

        reportCard.setAuditStatus(ReportCard.AuditStatus.REJECTED);
        repository.save(reportCard);

        log.info("报告卡审核拒绝: id={}", id);
    }

    @Override
    @Transactional
    public void withdrawAudit(String id) {
        log.info("撤回审核: id={}", id);

        ReportCard reportCard = repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        ReportCardAudit audit = auditRepository.findByReportCardId(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));

        if (audit.getAuditStatus() == ReportCardAudit.AuditStatus.PENDING) {
            throw new BusinessException("该报告卡为待审核状态，无需撤回");
        }

        audit.setAuditStatus(ReportCardAudit.AuditStatus.PENDING);
        audit.setAuditorId(null);
        audit.setAuditorName(null);
        audit.setAuditDate(null);
        audit.setRejectReason(null);
        auditRepository.save(audit);

        reportCard.setAuditStatus(ReportCard.AuditStatus.PENDING);
        repository.save(reportCard);

        log.info("报告卡审核已撤回: id={}", id);
    }

    @Override
    public PageResult<ReportCardDTO> getPendingCards(ReportCardQueryRequest request) {
        request.setStatus(ReportCard.AuditStatus.PENDING);
        return getReportCardList(request);
    }

    @Override
    public List<ReportCardDTO> getMyAuditedCards(String auditorId) {
        List<ReportCardAudit> audits = auditRepository.findByAuditorId(auditorId);

        return audits.stream()
                .map(audit -> {
                    ReportCard reportCard = repository.findById(audit.getReportCardId()).orElse(null);
                    if (reportCard != null && !reportCard.getDeleted()) {
                        ReportCardPatient patient = patientRepository.findByReportCardId(reportCard.getId()).orElse(null);
                        ReportCardDiagnosis diagnosis = diagnosisRepository.findByReportCardId(reportCard.getId()).orElse(null);
                        return ReportCardDTO.fromEntity(reportCard, patient, diagnosis, audit);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ReportCardStatisticsDTO getStatistics() {
        log.info("获取报卡统计数据");

        ReportCardStatisticsDTO dto = new ReportCardStatisticsDTO();
        dto.setTotal(repository.countByDeletedFalse());
        dto.setPending(repository.countByAuditStatusAndDeletedFalse(ReportCard.AuditStatus.PENDING));
        dto.setApproved(repository.countByAuditStatusAndDeletedFalse(ReportCard.AuditStatus.APPROVED));
        dto.setRejected(repository.countByAuditStatusAndDeletedFalse(ReportCard.AuditStatus.REJECTED));
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
            ReportCard.AuditStatus status = (ReportCard.AuditStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status.getDescription(), count);
        }

        return statistics;
    }

    @Override
    public long getCountByStatus(ReportCard.AuditStatus status) {
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
                LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
                results = repository.countByLast7Days(weekStart);
                labelMapping.put("Monday", "周一");
                labelMapping.put("Tuesday", "周二");
                labelMapping.put("Wednesday", "周三");
                labelMapping.put("Thursday", "周四");
                labelMapping.put("Friday", "周五");
                labelMapping.put("Saturday", "周六");
                labelMapping.put("Sunday", "周日");
                break;
            case "month":
                results = repository.countByWeeksInMonth();
                break;
            case "year":
                results = repository.countByMonthsInYear();
                break;
            default:
                throw new BusinessException("无效的周期参数: " + period);
        }

        List<TrendDataDTO> trendList = results.stream()
                .map(result -> {
                    String label = (String) result[0];
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

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 10,
                Sort.by(Sort.Direction.DESC, "updateTime"));
        List<ReportCard> recentCards = repository.findRecentUpdatedRecords(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return recentCards.stream()
                .map(card -> {
                    RecentActivityDTO dto = new RecentActivityDTO();
                    ReportCardAudit audit = auditRepository.findByReportCardId(card.getId()).orElse(null);

                    dto.setUser(audit != null && audit.getAuditorName() != null ?
                            audit.getAuditorName() : card.getDoctorName());
                    dto.setTarget(card.getDiseaseName() + "-" + card.getPatientName());
                    dto.setTime(card.getUpdateTime().format(formatter));

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

    @Override
    public boolean existsByInpatientNo(String inpatientNo) {
        return repository.existsByInpatientNo(inpatientNo);
    }

    @Override
    public boolean existsByOutpatientNo(String outpatientNo) {
        return repository.existsByOutpatientNo(outpatientNo);
    }
}
