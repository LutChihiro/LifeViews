package com.lifeviews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifeviews.dto.LoginRequest;
import com.lifeviews.dto.RegisterRequest;
import com.lifeviews.entity.User;
import com.lifeviews.mapper.UserMapper;
import com.lifeviews.service.UserService;
import com.lifeviews.utils.JwtUtil;
import com.lifeviews.vo.LoginVO;
import com.lifeviews.vo.UserProfileVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserProfileVO register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("phone already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(toNullable(request.getNickname()));
        user.setAvatarUrl(toNullable(request.getAvatarUrl()));
        user.setEmail(toNullable(request.getEmail()));
        user.setPhone(toNullable(request.getPhone()));
        user.setStatus(1);
        user.setDeleted(0);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        save(user);
        return toProfileVO(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()), false);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("username or password is incorrect");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new IllegalArgumentException("user is disabled");
        }
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        return new LoginVO(jwtUtil.generateToken(user.getId(), user.getUsername()));
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user does not exist");
        }
        return toProfileVO(user);
    }

    private boolean existsByUsername(String username) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0;
    }

    private boolean existsByEmail(String email) {
        return count(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0;
    }

    private boolean existsByPhone(String phone) {
        return count(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0;
    }

    private String toNullable(String value) {
        return StringUtils.hasText(value) ? value : null;
    }

    private UserProfileVO toProfileVO(User user) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
