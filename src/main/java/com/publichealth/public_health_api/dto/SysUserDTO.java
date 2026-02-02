package com.publichealth.public_health_api.dto;

import com.publichealth.public_health_api.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户数据传输对象
 * 用于返回给前端的用户信息 (不包含敏感字段如密码)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserDTO {

    private String id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private SysUser.UserRole role;
    private SysUser.UserStatus status;
    private String dataScope;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastLogin;

    /**
     * 从实体转换为DTO
     */
    public static SysUserDTO fromEntity(SysUser user) {
        if (user == null) {
            return null;
        }
        SysUserDTO dto = new SysUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setDataScope(user.getDataScope());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}
