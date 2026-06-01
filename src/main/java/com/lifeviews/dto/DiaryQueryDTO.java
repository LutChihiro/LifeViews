package com.lifeviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiaryQueryDTO {

    private String keyword;

    @Min(value = 1, message = "mood must be between 1 and 5")
    @Max(value = 5, message = "mood must be between 1 and 5")
    private Integer mood;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 0, message = "status must be 0 or 1")
    @Max(value = 1, message = "status must be 0 or 1")
    private Integer status;

    @Min(value = 1, message = "page must be at least 1")
    private Long page;

    @Min(value = 1, message = "pageNum must be at least 1")
    private Long pageNum;

    @Min(value = 1, message = "pageSize must be at least 1")
    @Max(value = 100, message = "pageSize cannot exceed 100")
    private Long pageSize;

    public Long getPageNum() {
        if (pageNum != null) {
            return pageNum;
        }
        if (page != null) {
            return page;
        }
        return 1L;
    }

    public Long getPageSize() {
        return pageSize == null ? 10L : pageSize;
    }
}
