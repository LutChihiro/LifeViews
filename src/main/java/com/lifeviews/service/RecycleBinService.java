package com.lifeviews.service;

import com.lifeviews.dto.RecycleBinQueryDTO;
import com.lifeviews.vo.RecycleBinPageVO;

public interface RecycleBinService {

    RecycleBinPageVO list(Long userId, RecycleBinQueryDTO query);

    boolean restore(Long userId, String module, Long id);

    boolean delete(Long userId, String module, Long id);

    boolean clear(Long userId, String module);
}
