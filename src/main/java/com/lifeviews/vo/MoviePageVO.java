package com.lifeviews.vo;

import lombok.Data;

import java.util.List;

@Data
public class MoviePageVO {

    private Long total;

    private List<MovieRecordVO> items;
}
