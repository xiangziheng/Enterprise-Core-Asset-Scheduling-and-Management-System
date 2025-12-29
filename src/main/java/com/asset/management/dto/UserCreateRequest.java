package com.asset.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新建用户请求
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 角色（ADMIN/USER）
     */
    private String role;

    /**
     * 状态（ACTIVE/DISABLED）
     */
    private String status;
}
