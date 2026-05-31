package com.lifeviews.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private String nickname;

    private String avatarUrl;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createdAt;
}
