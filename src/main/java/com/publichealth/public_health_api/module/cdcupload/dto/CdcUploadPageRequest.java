package com.publichealth.public_health_api.module.cdcupload.dto;

import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * CDC上报分页查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUploadPageRequest {

    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    /**
     * 搜索关键词（患者姓名/诊断名称）
     */
    private String keyword;

    /**
     * 上报状态筛选
     */
    private UploadStatus uploadStatus;

    /**
     * 科室筛选
     */
    private String department;

    /**
     * 填卡日期开始
     */
    private LocalDate fillDateStart;

    /**
     * 填卡日期结束
     */
    private LocalDate fillDateEnd;
}
