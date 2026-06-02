package com.lifeviews.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("movie_record")
public class MovieRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
