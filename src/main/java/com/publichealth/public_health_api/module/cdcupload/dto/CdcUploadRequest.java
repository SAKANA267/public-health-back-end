package com.publichealth.public_health_api.module.cdcupload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量上报请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUploadRequest {

    @NotEmpty(message = "报告卡ID列表不能为空")
    private List<String> reportCardIds;

    @NotBlank(message = "操作人ID不能为空")
    private String operatorId;
}
