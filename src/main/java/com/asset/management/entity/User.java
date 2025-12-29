package com.asset.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（课程项目可使用明文）
     */
    @JsonIgnore
    private String password;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    public enum UserRole {
        ADMIN,
        USER
    }

    public enum UserStatus {
        ACTIVE,
        DISABLED
    }
}
