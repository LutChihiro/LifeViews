package com.lifeviews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

    @NotBlank(message = "oldPassword cannot be blank")
    private String oldPassword;

    @NotBlank(message = "newPassword cannot be blank")
    @Size(min = 6, max = 50, message = "newPassword length must be between 6 and 50")
    private String newPassword;

    @NotBlank(message = "confirmPassword cannot be blank")
    private String confirmPassword;
}
