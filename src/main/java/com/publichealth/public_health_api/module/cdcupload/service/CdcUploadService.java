package com.publichealth.public_health_api.module.cdcupload.service;

import com.publichealth.public_health_api.common.PageResult;
import com.publichealth.public_health_api.module.cdcupload.dto.*;

/**
 * CDC上报服务接口
 */
public interface CdcUploadService {

    /**
     * 分页查询已审核通过且可上报的报告卡列表
     */
    PageResult<CdcUploadDTO> getApprovedReportCards(CdcUploadPageRequest request);

    /**
     * 上报单个报告卡
     */
    void uploadSingle(String reportCardId, String operatorId);

    /**
     * 批量上报报告卡
     */
    CdcUploadStatistics batchUpload(CdcUploadRequest request);

    /**
     * 重试上报失败的报告卡
     */
    void retryUpload(String reportCardId, String operatorId);

    /**
     * 获取上报统计信息
     */
    CdcUploadStatistics getUploadStatistics();
}
