package com.lifeviews.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "username cannot be blank")
    @Size(min = 3, max = 50, message = "username length must be between 3 and 50")
    private String username;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 50, message = "password length must be between 6 and 50")
    private String password;

    @Email(message = "email format is invalid")
    private String email;

    @Size(max = 50, message = "nickname length cannot exceed 50")
    private String nickname;

    @Size(max = 512, message = "avatarUrl length cannot exceed 512")
    private String avatarUrl;

    @Size(max = 32, message = "phone length cannot exceed 32")
    private String phone;
}
