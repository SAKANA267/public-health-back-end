package com.publichealth.public_health_api.module.cdcupload.service.impl;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.exception.BusinessException;
import com.publichealth.public_health_api.module.cdcupload.dto.*;
import com.publichealth.public_health_api.module.cdcupload.entity.CdcUpload;
import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import com.publichealth.public_health_api.module.cdcupload.repository.CdcUploadRepository;
import com.publichealth.public_health_api.module.cdcupload.service.CdcUploadService;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardPatient;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardAuditRepository;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardPatientRepository;
import com.publichealth.public_health_api.module.reportcard.repository.ReportCardRepository;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CDC上报服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CdcUploadServiceImpl implements CdcUploadService {

    private final CdcUploadRepository cdcUploadRepository;
    private final ReportCardRepository reportCardRepository;
    private final ReportCardPatientRepository patientRepository;
    private final ReportCardAuditRepository auditRepository;
    private final SysUserRepository sysUserRepository;

    @Override
    @Transactional
    public PageResult<CdcUploadDTO> getApprovedReportCards(CdcUploadPageRequest request) {
        log.info("查询已审核通过可上报的报告卡列表: page={}, size={}", request.getPage(), request.getSize());

        // 确保所有已审核通过的报告卡都有上报记录
        ensureUploadRecordsExist();

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Page<CdcUpload> page = cdcUploadRepository.findApprovedWithUploadStatus(
                request.getKeyword(),
                request.getUploadStatus(),
                request.getDepartment(),
                request.getFillDateStart(),
                request.getFillDateEnd(),
                pageable
        );

        // 批量查询关联的 ReportCard
        List<String> reportCardIds = page.getContent().stream()
                .map(CdcUpload::getReportCardId)
                .toList();
        Map<String, ReportCard> reportCardMap = reportCardRepository.findAllById(reportCardIds).stream()
                .collect(Collectors.toMap(ReportCard::getId, Function.identity()));

        List<CdcUploadDTO> records = page.getContent().stream()
                .map(upload -> {
                    ReportCard rc = reportCardMap.get(upload.getReportCardId());
                    ReportCardPatient patient = patientRepository.findByReportCardId(upload.getReportCardId()).orElse(null);
                    ReportCardAudit audit = auditRepository.findByReportCardId(upload.getReportCardId()).orElse(null);
                    return CdcUploadDTO.fromEntity(upload, rc, patient, audit);
                })
                .toList();

        return PageResult.of(request.getPage(), request.getSize(), page.getTotalElements(), records);
    }

    @Override
    @Transactional
    public void uploadSingle(String reportCardId, String operatorId) {
        log.info("上报单个报告卡: reportCardId={}, operatorId={}", reportCardId, operatorId);

        // 1. 验证报告卡存在
        ReportCard reportCard = reportCardRepository.findById(reportCardId)
                .orElseThrow(() -> new BusinessException("报告卡不存在"));

        // 2. 验证审核状态
        if (reportCard.getAuditStatus() != ReportCard.AuditStatus.APPROVED) {
            throw new BusinessException("报告卡未审核通过，无法上报");
        }

        // 3. 获取或创建上报记录
        CdcUpload cdcUpload = cdcUploadRepository.findByReportCardIdAndDeletedFalse(reportCardId)
                .orElseGet(() -> {
                    CdcUpload newUpload = new CdcUpload();
                    newUpload.setReportCardId(reportCardId);
                    return newUpload;
                });

        // 4. 验证当前上报状态
        if (cdcUpload.getUploadStatus() == UploadStatus.UPLOADING) {
            throw new BusinessException("报告卡正在上报中，请勿重复提交");
        }
        if (cdcUpload.getUploadStatus() == UploadStatus.UPLOADED) {
            throw new BusinessException("报告卡已上报成功，无需重复上报");
        }

        // 5. 更新状态为上报中
        cdcUpload.setUploadStatus(UploadStatus.UPLOADING);
        cdcUploadRepository.save(cdcUpload);

        // 6. 查询操作人姓名
        String operatorName = null;
        if (operatorId != null) {
            operatorName = sysUserRepository.findById(operatorId)
                    .map(SysUser::getName)
                    .orElse(null);
        }

        // 7. 执行上报（当前为模拟实现，直接标记成功）
        try {
            log.info("模拟CDC上报: reportCardId={}", reportCardId);
            // TODO: 实际调用CDC接口 cdcApiClient.uploadToCdc(reportCard);
            String serialNo = "CDC-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-" + reportCardId.substring(0, Math.min(8, reportCardId.length()));

            cdcUpload.setUploadStatus(UploadStatus.UPLOADED);
            cdcUpload.setUploadTime(LocalDateTime.now());
            cdcUpload.setUploadOperator(operatorId);
            cdcUpload.setUploadOperatorName(operatorName);
            cdcUpload.setCdcSerialNo(serialNo);
            cdcUpload.setFailReason(null);
        } catch (Exception e) {
            log.error("CDC上报失败: reportCardId={}, error={}", reportCardId, e.getMessage());
            cdcUpload.setUploadStatus(UploadStatus.UPLOAD_FAILED);
            cdcUpload.setFailReason(e.getMessage());
            cdcUpload.setRetryCount(cdcUpload.getRetryCount() + 1);
        }
        cdcUploadRepository.save(cdcUpload);
    }

    @Override
    @Transactional
    public CdcUploadStatistics batchUpload(CdcUploadRequest request) {
        log.info("批量上报报告卡: count={}, operatorId={}", request.getReportCardIds().size(), request.getOperatorId());

        long successCount = 0;
        long failCount = 0;

        for (String reportCardId : request.getReportCardIds()) {
            try {
                uploadSingle(reportCardId, request.getOperatorId());
                successCount++;
            } catch (Exception e) {
                log.warn("批量上报-单张上报失败: reportCardId={}, error={}", reportCardId, e.getMessage());
                failCount++;
            }
        }

        CdcUploadStatistics result = new CdcUploadStatistics();
        result.setTotal((long) request.getReportCardIds().size());
        result.setNotUploaded(0L);
        result.setUploading(0L);
        result.setUploaded(successCount);
        result.setUploadFailed(failCount);
        return result;
    }

    @Override
    @Transactional
    public void retryUpload(String reportCardId, String operatorId) {
        log.info("重试上报: reportCardId={}, operatorId={}", reportCardId, operatorId);

        CdcUpload cdcUpload = cdcUploadRepository.findByReportCardIdAndDeletedFalse(reportCardId)
                .orElseThrow(() -> new BusinessException("上报记录不存在"));

        // 验证状态必须是失败
        if (cdcUpload.getUploadStatus() != UploadStatus.UPLOAD_FAILED) {
            throw new BusinessException("只能重试上报失败的记录");
        }

        // 验证重试次数
        if (cdcUpload.getRetryCount() >= 3) {
            throw new BusinessException("已达到最大重试次数(3次)，无法继续重试");
        }

        // 重置状态为未上报，然后执行上报
        cdcUpload.setUploadStatus(UploadStatus.NOT_UPLOADED);
        cdcUploadRepository.save(cdcUpload);

        // 调用上报逻辑
        uploadSingle(reportCardId, operatorId);
    }

    @Override
    @Transactional
    public CdcUploadStatistics getUploadStatistics() {
        log.info("获取CDC上报统计信息");

        // 确保所有已审核通过的报告卡都有上报记录
        ensureUploadRecordsExist();

        long total = cdcUploadRepository.countByDeletedFalse();
        long notUploaded = cdcUploadRepository.countByUploadStatusAndDeletedFalse(UploadStatus.NOT_UPLOADED);
        long uploading = cdcUploadRepository.countByUploadStatusAndDeletedFalse(UploadStatus.UPLOADING);
        long uploaded = cdcUploadRepository.countByUploadStatusAndDeletedFalse(UploadStatus.UPLOADED);
        long uploadFailed = cdcUploadRepository.countByUploadStatusAndDeletedFalse(UploadStatus.UPLOAD_FAILED);

        CdcUploadStatistics stats = new CdcUploadStatistics();
        stats.setTotal(total);
        stats.setNotUploaded(notUploaded);
        stats.setUploading(uploading);
        stats.setUploaded(uploaded);
        stats.setUploadFailed(uploadFailed);
        return stats;
    }

    /**
     * 确保所有已审核通过的报告卡都有对应的 CdcUpload 记录
     * 使用 INSERT IGNORE 原子操作，并发安全
     */
    @Transactional
    public void ensureUploadRecordsExist() {
        int count = cdcUploadRepository.insertMissingUploadRecords();
        if (count > 0) {
            log.info("为 {} 张已审核通过但无上报记录的报告卡创建记录", count);
        }
    }
}
