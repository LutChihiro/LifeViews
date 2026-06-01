package com.lifeviews.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileDTO {

    @NotBlank(message = "nickname cannot be blank")
    @Size(max = 64, message = "nickname length cannot exceed 64")
    private String nickname;

    @Email(message = "email format is invalid")
    private String email;

    @Size(max = 32, message = "phone length cannot exceed 32")
    private String phone;

    @Size(max = 512, message = "avatarUrl length cannot exceed 512")
    private String avatarUrl;
}
