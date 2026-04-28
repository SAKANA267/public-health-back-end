package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardAudit;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCardDiagnosis;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 传染病报告卡查询请求DTO
 * 用于列表查询和筛选
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardQueryRequest {

    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    private String keyword;

    private ReportCard.AuditStatus status;

    private ReportCardAudit.AssignStatus assignStatus;

    private ReportCard.ReportCategory reportCategory;

    private ReportCard.ReportStatus reportStatus;

    private String hospitalArea;

    private String department;

    private String auditorId;

    private ReportCardDiagnosis.CaseType caseType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean includeDeleted = false;
}
