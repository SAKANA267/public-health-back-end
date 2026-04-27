package com.publichealth.public_health_api.module.cdcupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CDC上报统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUploadStatistics {

    /**
     * 已审核通过的报告卡总数
     */
    private Long total;

    /**
     * 未上报数量
     */
    private Long notUploaded;

    /**
     * 上报中数量
     */
    private Long uploading;

    /**
     * 已上报成功数量
     */
    private Long uploaded;

    /**
     * 上报失败数量
     */
    private Long uploadFailed;
}
