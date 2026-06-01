package com.lifeviews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "currentPassword cannot be blank")
    private String currentPassword;

    @NotBlank(message = "newPassword cannot be blank")
    @Size(min = 6, message = "newPassword length must be at least 6")
    private String newPassword;

    @NotBlank(message = "confirmPassword cannot be blank")
    private String confirmPassword;
}
