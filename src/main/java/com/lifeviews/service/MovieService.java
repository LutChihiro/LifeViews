package com.lifeviews.service;

import com.lifeviews.dto.MovieCreateDTO;
import com.lifeviews.dto.MovieQueryDTO;
import com.lifeviews.dto.MovieUpdateDTO;
import com.lifeviews.vo.MoviePageVO;
import com.lifeviews.vo.MovieRecordVO;

public interface MovieService {

    MovieRecordVO create(Long userId, MovieCreateDTO request);

    MovieRecordVO update(Long userId, Long id, MovieUpdateDTO request);

    void delete(Long userId, Long id);

    MovieRecordVO detail(Long userId, Long id);

    MoviePageVO list(Long userId, MovieQueryDTO query);
}
