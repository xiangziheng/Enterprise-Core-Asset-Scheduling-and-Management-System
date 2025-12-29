package com.asset.management.controller;

import com.asset.management.dto.Result;
import com.asset.management.dto.UserCreateRequest;
import com.asset.management.entity.User;
import com.asset.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    @PostMapping
    public Result<User> create(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword());
        user.setRole(parseRole(request.getRole()));
        user.setStatus(parseStatus(request.getStatus()));
        return Result.success(userService.createUser(user));
    }

    private User.UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return User.UserRole.USER;
        }
        try {
            return User.UserRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("角色不合法");
        }
    }

    private User.UserStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return User.UserStatus.ACTIVE;
        }
        try {
            return User.UserStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("状态不合法");
        }
    }
}
