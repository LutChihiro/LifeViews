package com.lifeviews.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifeviews.dto.DiaryCreateDTO;
import com.lifeviews.dto.DiaryQueryDTO;
import com.lifeviews.dto.DiaryUpdateDTO;
import com.lifeviews.vo.DiaryDetailVO;
import com.lifeviews.vo.DiaryListVO;
import com.lifeviews.vo.DiaryUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryService {

    Page<DiaryListVO> list(Long userId, DiaryQueryDTO query);

    DiaryDetailVO detail(Long userId, Long id);

    DiaryDetailVO create(Long userId, DiaryCreateDTO request);

    DiaryDetailVO update(Long userId, Long id, DiaryUpdateDTO request);

    void delete(Long userId, Long id);

    DiaryUploadVO uploadImage(MultipartFile file);
}
