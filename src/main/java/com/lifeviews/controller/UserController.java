package com.lifeviews.controller;

import com.lifeviews.common.Result;
import com.lifeviews.common.UserContext;
import com.lifeviews.dto.UpdateUserProfileDTO;
import com.lifeviews.service.UserService;
import com.lifeviews.vo.UserProfileVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<UserProfileVO> profile() {
        return Result.success(userService.getProfile(UserContext.getCurrentUserId()));
    }

    @PutMapping("/profile")
    public Result<UserProfileVO> updateProfile(@Valid @RequestBody UpdateUserProfileDTO request) {
        return Result.success(userService.updateProfile(UserContext.getCurrentUserId(), request));
    }
}
