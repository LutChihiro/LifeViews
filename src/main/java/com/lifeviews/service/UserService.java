package com.lifeviews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifeviews.dto.LoginRequest;
import com.lifeviews.dto.RegisterRequest;
import com.lifeviews.entity.User;
import com.lifeviews.vo.LoginVO;
import com.lifeviews.vo.UserProfileVO;

public interface UserService extends IService<User> {

    UserProfileVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    UserProfileVO getProfile(Long userId);
}
