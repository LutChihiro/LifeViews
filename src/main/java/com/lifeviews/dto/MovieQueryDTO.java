package com.lifeviews.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovieQueryDTO {

    private String keyword;

    private String category;

    private String region;

    @Min(value = 0, message = "watchStatus must be 0 or 1")
    @Max(value = 1, message = "watchStatus must be 0 or 1")
    private Integer watchStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "minRating must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "minRating must be between 0 and 10")
    private BigDecimal minRating;

    @DecimalMin(value = "0.0", message = "maxRating must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "maxRating must be between 0 and 10")
    private BigDecimal maxRating;

    @Min(value = 1, message = "pageNum must be at least 1")
    private Long pageNum;

    @Min(value = 1, message = "pageSize must be at least 1")
    @Max(value = 100, message = "pageSize cannot exceed 100")
    private Long pageSize;

    public Long getPageNum() {
        return pageNum == null ? 1L : pageNum;
    }

    public Long getPageSize() {
        return pageSize == null ? 10L : pageSize;
    }
}
