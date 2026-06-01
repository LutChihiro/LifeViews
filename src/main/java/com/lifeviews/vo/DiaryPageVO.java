package com.lifeviews.vo;

import lombok.Data;

import java.util.List;

@Data
public class DiaryPageVO {

    private Long total;

    private List<DiaryListVO> items;
}
