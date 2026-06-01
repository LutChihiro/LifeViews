package com.lifeviews.vo;

import lombok.Data;

import java.util.List;

@Data
public class RecycleBinPageVO {

    private Long total;

    private List<RecycleBinItemVO> items;
}
