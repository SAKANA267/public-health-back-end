package com.publichealth.public_health_api.module.reportcard.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.reportcard.dto.*;
import com.publichealth.public_health_api.module.reportcard.entity.ReportCard;

import java.util.List;
import java.util.Map;

/**
 * 传染病报告卡服务接口
 * 定义报告卡相关的业务操作
 */
public interface ReportCardService {

    // ============================================
    // 基础 CRUD 操作
    // ============================================

    /**
     * 创建报告卡
     * @param request 创建请求
     * @return 创建的DTO
     */
    ReportCardDTO createReportCard(CreateReportCardRequest request);

    /**
     * 根据ID获取报告卡
     * @param id 报告卡ID
     * @return DTO
     */
    ReportCardDTO getReportCardById(String id);

    /**
     * 根据住院号获取报告卡
     * @param inpatientNo 住院号
     * @return DTO
     */
    ReportCardDTO getReportCardByInpatientNo(String inpatientNo);

    /**
     * 更新报告卡
     * 注意: 仅允许更新待审核状态的报告卡
     * @param id 报告卡ID
     * @param request 更新请求
     * @return 更新后的DTO
     */
    ReportCardDTO updateReportCard(String id, UpdateReportCardRequest request);

    /**
     * 删除报告卡 (逻辑删除)
     * @param id 报告卡ID
     */
    void deleteReportCard(String id);

    /**
     * 批量删除报告卡 (逻辑删除)
     * @param ids 报告卡ID列表
     */
    void batchDeleteReportCards(List<String> ids);

    // ============================================
    // 查询操作
    // ============================================

    /**
     * 分页查询报告卡列表
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<ReportCardDTO> getReportCardList(ReportCardQueryRequest request);

    /**
     * 搜索报告卡
     * @param keyword 搜索关键词
     * @return DTO列表
     */
    List<ReportCardDTO> searchReportCards(String keyword);

    /**
     * 根据状态查询报告卡列表
     * @param status 报告状态
     * @return DTO列表
     */
    List<ReportCardDTO> getReportCardsByStatus(ReportCard.ReportStatus status);

    /**
     * 根据院区查询报告卡列表
     * @param hospitalArea 院区
     * @return DTO列表
     */
    List<ReportCardDTO> getReportCardsByHospitalArea(String hospitalArea);

    /**
     * 根据科室查询报告卡列表
     * @param department 科室
     * @return DTO列表
     */
    List<ReportCardDTO> getReportCardsByDepartment(String department);

    // ============================================
    // 审核业务操作
    // ============================================

    /**
     * 审核通过
     * @param id 报告卡ID
     * @param auditorId 审核人ID
     * @param remark 审核备注
     */
    void approveReportCard(String id, String auditorId, String remark);

    /**
     * 审核拒绝
     * @param id 报告卡ID
     * @param auditorId 审核人ID
     * @param remark 审核备注 (建议填写拒绝原因)
     */
    void rejectReportCard(String id, String auditorId, String remark);

    /**
     * 分页查询待审核报告卡列表
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<ReportCardDTO> getPendingCards(ReportCardQueryRequest request);

    /**
     * 获取我审核的报告卡列表
     * @param auditorId 审核人ID
     * @return DTO列表
     */
    List<ReportCardDTO> getMyAuditedCards(String auditorId);

    // ============================================
    // 统计查询
    // ============================================

    /**
     * 获取各状态统计数量
     * @return Map<状态描述, 数量>
     */
    Map<String, Long> getStatusStatistics();

    /**
     * 获取指定状态的数量
     * @param status 报告状态
     * @return 数量
     */
    long getCountByStatus(ReportCard.ReportStatus status);

    // ============================================
    // 存在性检查
    // ============================================

    /**
     * 检查住院号是否存在
     * @param inpatientNo 住院号
     * @return 是否存在
     */
    boolean existsByInpatientNo(String inpatientNo);

    /**
     * 检查门诊号是否存在
     * @param outpatientNo 门诊号
     * @return 是否存在
     */
    boolean existsByOutpatientNo(String outpatientNo);
}
