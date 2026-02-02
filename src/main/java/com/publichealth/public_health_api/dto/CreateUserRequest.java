package com.publichealth.public_health_api.dto;

import com.publichealth.public_health_api.entity.SysUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户请求DTO
 * 用于接收创建用户时的请求数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
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
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 角色
     */
    private SysUser.UserRole role = SysUser.UserRole.USER;

    /**
     * 状态
     */
    private SysUser.UserStatus status = SysUser.UserStatus.ACTIVE;

    /**
     * 数据范围
     */
    private String dataScope;
}
