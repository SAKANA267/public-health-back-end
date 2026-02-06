package com.publichealth.public_health_api.module.operationlog.dto;

import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;
import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogDTO {

    private String id;
    private String userId;
    private String username;
    private String module;
    private OperationType operationType;
    private String operation;
    private String method;
    private String params;
    private String ipAddress;
    private String location;
    private OperationStatus status;
    private String errorMsg;
    private Long costTime;
    private LocalDateTime createTime;

    /**
     * 从实体转换为DTO
     */
    public static OperationLogDTO fromEntity(OperationLog log) {
        if (log == null) {
            return null;
        }
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(log.getId());
        dto.setUserId(log.getUserId());
        dto.setUsername(log.getUsername());
        dto.setModule(log.getModule());
        dto.setOperationType(log.getOperationType());
        dto.setOperation(log.getOperation());
        dto.setMethod(log.getMethod());
        dto.setParams(log.getParams());
        dto.setIpAddress(log.getIpAddress());
        dto.setLocation(log.getLocation());
        dto.setStatus(log.getStatus());
        dto.setErrorMsg(log.getErrorMsg());
        dto.setCostTime(log.getCostTime());
        dto.setCreateTime(log.getCreateTime());
        return dto;
    }
}
