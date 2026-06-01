package com.lifeviews.vo;

import lombok.Data;

@Data
public class DiaryImageVO {

    private Long id;

    private String imageUrl;

    private String imageName;

    private Integer sortOrder;
}
