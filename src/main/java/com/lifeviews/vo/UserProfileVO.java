package com.lifeviews.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String email;

    private String nickname;

    private LocalDateTime createTime;
}
