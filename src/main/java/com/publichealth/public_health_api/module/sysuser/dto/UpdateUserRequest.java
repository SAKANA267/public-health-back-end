package com.publichealth.public_health_api.module.sysuser.dto;

import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户请求DTO
 * 用于接收更新用户时的请求数据
 * 所有字段都是可选的，只更新提供的字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名不能超过50个字符")
    private String name;

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 角色
     */
    private SysUser.UserRole role;

    /**
     * 状态
     */
    private SysUser.UserStatus status;

    /**
     * 数据范围
     */
    private String dataScope;
}
