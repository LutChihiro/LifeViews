package com.lifeviews.controller;

import com.lifeviews.common.Result;
import com.lifeviews.common.UserContext;
import com.lifeviews.service.UserService;
import com.lifeviews.vo.UserProfileVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
