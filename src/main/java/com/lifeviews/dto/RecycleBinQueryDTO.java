package com.lifeviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecycleBinQueryDTO {

    @NotBlank(message = "module cannot be blank")
    private String module;

    @Min(value = 1, message = "page must be at least 1")
    private Long page = 1L;

    @Min(value = 1, message = "pageSize must be at least 1")
    @Max(value = 100, message = "pageSize cannot exceed 100")
    private Long pageSize = 10L;

    private String keyword;
}
