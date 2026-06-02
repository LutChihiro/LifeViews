package com.lifeviews.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MovieRecordVO {

    private Long id;

    private String movieName;

    private String posterUrl;

    private String category;

    private String region;

    private Integer releaseYear;

    private LocalDate watchDate;

    private String watchPlace;

    private BigDecimal rating;

    private String review;

    private Integer watchStatus;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
