package com.publichealth.public_health_api.module.reportcard.dto;

import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 传染病报告卡查询请求DTO
 * 用于列表查询和筛选
 * 统一查询标准：startTime/endTime 使用 LocalDateTime 类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardQueryRequest {

    // ============================================
    // 分页参数
    // ============================================

    /**
     * 页码 (从1开始)
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    // ============================================
    // 搜索参数
    // ============================================

    /**
     * 搜索关键词
     * 可搜索: 患者姓名、诊断名称、住院号
     */
    private String keyword;

    // ============================================
    // 筛选参数
    // ============================================

    /**
     * 状态筛选
     */
    private ReportCard.ReportStatus status;

    /**
     * 院区筛选
     */
    private String hospitalArea;

    /**
     * 科室筛选
     */
    private String department;

    /**
     * 审核人ID筛选
     */
    private String auditorId;

    /**
     * 开始时间 (查询创建时间范围)
     */
    private LocalDateTime startTime;

    /**
     * 结束时间 (查询创建时间范围)
     */
    private LocalDateTime endTime;

    /**
     * 是否包含已删除记录
     */
    private Boolean includeDeleted = false;
}
