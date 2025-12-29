package com.asset.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前登录用户
 */
@Data
@AllArgsConstructor
public class AuthUser {
    private Long id;
    private String username;
    private String role;
}
