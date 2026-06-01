package com.lifeviews.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lifeviews.entity.User;
import com.lifeviews.mapper.SysUserMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UserRepository {

    private final SysUserMapper userMapper;

    public UserRepository(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    public User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    public boolean existsByUsername(String username) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0;
    }

    public boolean existsByEmail(String email) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)) > 0;
    }

    public boolean existsByEmailAndIdNot(String email, Long userId) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .ne(User::getId, userId)) > 0;
    }

    public boolean existsByPhone(String phone) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)) > 0;
    }

    public boolean existsByPhoneAndIdNot(String phone, Long userId) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .ne(User::getId, userId)) > 0;
    }

    public void create(User user) {
        userMapper.insert(user);
    }

    public void updateLastLoginTime(Long userId, LocalDateTime lastLoginTime) {
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastLoginTime, lastLoginTime)
                .set(User::getUpdatedAt, lastLoginTime));
    }

    public void updateProfile(User user) {
        LocalDateTime now = LocalDateTime.now();
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getNickname, user.getNickname())
                .set(User::getEmail, user.getEmail())
                .set(User::getPhone, user.getPhone())
                .set(User::getAvatarUrl, user.getAvatarUrl())
                .set(User::getUpdatedAt, now));
    }
}
