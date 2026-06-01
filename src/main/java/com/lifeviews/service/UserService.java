package com.lifeviews.service;

import com.lifeviews.dto.LoginRequest;
import com.lifeviews.dto.RegisterRequest;
import com.lifeviews.dto.UpdateUserProfileDTO;
import com.lifeviews.vo.LoginVO;
import com.lifeviews.vo.UserProfileVO;

public interface UserService {

    UserProfileVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    UserProfileVO getProfile(Long userId);

    UserProfileVO updateProfile(Long userId, UpdateUserProfileDTO request);
}
