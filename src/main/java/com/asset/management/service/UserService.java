package com.asset.management.service;

import com.asset.management.entity.User;
import com.asset.management.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userMapper.findByUsername(username.trim());
    }

    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (user.getRole() == null) {
            user.setRole(User.UserRole.USER);
        }
        if (user.getStatus() == null) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        userMapper.insert(user);
        return user;
    }
}
