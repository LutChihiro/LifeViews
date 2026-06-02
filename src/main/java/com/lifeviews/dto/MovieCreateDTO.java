package com.lifeviews.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovieCreateDTO {

    @NotBlank(message = "movieName cannot be blank")
    @Size(max = 255, message = "movieName length cannot exceed 255")
    private String movieName;

    @Size(max = 512, message = "posterUrl length cannot exceed 512")
    private String posterUrl;

    @Size(max = 128, message = "category length cannot exceed 128")
    private String category;

    @Size(max = 64, message = "region length cannot exceed 64")
    private String region;

    private Integer releaseYear;

    private LocalDate watchDate;

    @Size(max = 255, message = "watchPlace length cannot exceed 255")
    private String watchPlace;

    @DecimalMin(value = "0.0", message = "rating must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "rating must be between 0 and 10")
    private BigDecimal rating;

    private String review;

    @Min(value = 0, message = "watchStatus must be 0 or 1")
    @Max(value = 1, message = "watchStatus must be 0 or 1")
    private Integer watchStatus;
}
