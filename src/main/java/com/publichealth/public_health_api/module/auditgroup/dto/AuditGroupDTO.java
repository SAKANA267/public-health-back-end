package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审核组数据传输对象
 * 用于返回给前端的审核组信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditGroupDTO {

    private String id;
    private String groupName;
    private String groupCode;
    private String description;
    private String leaderId;
    private String leaderName;  // 组长姓名
    private AuditGroup.AuditGroupStatus status;
    private Integer memberCount;  // 成员数量
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 从实体转换为DTO
     */
    public static AuditGroupDTO fromEntity(AuditGroup group) {
        if (group == null) {
            return null;
        }
        AuditGroupDTO dto = new AuditGroupDTO();
        dto.setId(group.getId());
        dto.setGroupName(group.getGroupName());
        dto.setGroupCode(group.getGroupCode());
        dto.setDescription(group.getDescription());
        dto.setLeaderId(group.getLeaderId());
        dto.setStatus(group.getStatus());
        dto.setCreateTime(group.getCreateTime());
        dto.setUpdateTime(group.getUpdateTime());
        return dto;
    }
}
