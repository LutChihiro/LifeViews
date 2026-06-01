package com.lifeviews.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class DiaryDetailVO {

    private Long id;

    private String title;

    private String content;

    private LocalDate diaryDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime diaryTime;

    private Integer mood;

    private String moodText;

    private String weather;

    private Integer isPinned;

    private Integer wordCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<DiaryImageVO> images;
}
