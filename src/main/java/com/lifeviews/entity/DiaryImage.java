package com.lifeviews.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("diary_image")
public class DiaryImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long diaryId;

    private Long userId;

    private String imageUrl;

    private String imageName;

    private Integer sortOrder;

    private LocalDateTime createdAt;
}
