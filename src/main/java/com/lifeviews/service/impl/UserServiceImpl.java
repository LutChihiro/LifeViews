package com.lifeviews.service.impl;

import com.lifeviews.dto.LoginRequest;
import com.lifeviews.dto.RegisterRequest;
import com.lifeviews.entity.User;
import com.lifeviews.repository.UserRepository;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserProfileVO register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        if (StringUtils.hasText(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
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
        userRepository.create(user);
        return toProfileVO(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("username or password is incorrect");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new IllegalArgumentException("user is disabled");
        }
        LocalDateTime lastLoginTime = LocalDateTime.now();
        userRepository.updateLastLoginTime(user.getId(), lastLoginTime);
        user.setLastLoginTime(lastLoginTime);
        return new LoginVO(jwtUtil.generateToken(user.getId(), user.getUsername()));
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user does not exist");
        }
        return toProfileVO(user);
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
