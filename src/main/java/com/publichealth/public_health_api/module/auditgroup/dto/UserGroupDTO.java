package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.auditgroup.entity.AuditGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户所属组数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDTO {

    private String groupId;
    private String groupName;
    private String groupCode;
    private String description;
    private AuditGroup.AuditGroupStatus status;
    private Boolean isLeader;  // 是否为组长
    private LocalDateTime joinTime;

    /**
     * 从AuditGroup实体转换
     */
    public static UserGroupDTO fromGroup(AuditGroup group, Boolean isLeader, LocalDateTime joinTime) {
        if (group == null) {
            return null;
        }
        UserGroupDTO dto = new UserGroupDTO();
        dto.setGroupId(group.getId());
        dto.setGroupName(group.getGroupName());
        dto.setGroupCode(group.getGroupCode());
        dto.setDescription(group.getDescription());
        dto.setStatus(group.getStatus());
        dto.setIsLeader(isLeader);
        dto.setJoinTime(joinTime);
        return dto;
    }
}
