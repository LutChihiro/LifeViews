package com.lifeviews.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
