package com.lifeviews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiaryImageDTO {

    @NotBlank(message = "imageUrl cannot be blank")
    @Size(max = 512, message = "imageUrl length cannot exceed 512")
    private String imageUrl;

    @Size(max = 255, message = "imageName length cannot exceed 255")
    private String imageName;

    private Integer sortOrder;
}
