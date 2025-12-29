package com.asset.management.service;

import com.asset.management.dto.AuthResponse;
import com.asset.management.dto.AuthUser;
import com.asset.management.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final Map<String, AuthUser> sessions = new ConcurrentHashMap<>();

    public AuthResponse login(String username, String password) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() != User.UserStatus.ACTIVE) {
            return null;
        }
        if (!Objects.equals(user.getPassword(), password)) {
            return null;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        String role = user.getRole() == null ? User.UserRole.USER.name() : user.getRole().name();
        AuthUser authUser = new AuthUser(user.getId(), user.getUsername(), role);
        sessions.put(token, authUser);
        return new AuthResponse(token, authUser.getUsername(), authUser.getRole());
    }

    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            sessions.remove(token);
        }
    }

    public AuthUser getAuthUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return sessions.get(token);
    }
}
