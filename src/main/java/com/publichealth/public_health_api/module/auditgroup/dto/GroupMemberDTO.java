package com.publichealth.public_health_api.module.auditgroup.dto;

import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 组成员数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {

    private String userId;
    private String username;
    private String name;
    private String email;
    private String phone;
    private SysUser.UserRole role;
    private SysUser.UserStatus status;
    private LocalDateTime joinTime;

    /**
     * 从SysUser实体转换
     */
    public static GroupMemberDTO fromUser(SysUser user, LocalDateTime joinTime) {
        if (user == null) {
            return null;
        }
        GroupMemberDTO dto = new GroupMemberDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setJoinTime(joinTime);
        return dto;
    }
}
