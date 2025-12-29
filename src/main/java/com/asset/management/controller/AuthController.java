package com.asset.management.controller;

import com.asset.management.dto.AuthResponse;
import com.asset.management.dto.AuthUser;
import com.asset.management.dto.LoginRequest;
import com.asset.management.dto.Result;
import com.asset.management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getUsername(), request.getPassword());
        if (response == null) {
            return Result.error(401, "用户名或密码错误");
        }
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Object token = request.getAttribute("authToken");
        if (token instanceof String) {
            authService.logout((String) token);
        }
        return Result.success();
    }

    @GetMapping("/me")
    public Result<AuthUser> me(HttpServletRequest request) {
        Object user = request.getAttribute("authUser");
        if (user instanceof AuthUser) {
            return Result.success((AuthUser) user);
        }
        return Result.error(401, "未登录");
    }
}
