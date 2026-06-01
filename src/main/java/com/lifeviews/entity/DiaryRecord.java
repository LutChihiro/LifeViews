package com.lifeviews.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("diary_record")
public class DiaryRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private LocalDate diaryDate;

    private LocalTime diaryTime;

    private Integer mood;

    private String moodText;

    private String weather;

    private Integer isPinned;

    private Integer wordCount;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer deleted;

    private LocalDateTime deletedAt;
}
