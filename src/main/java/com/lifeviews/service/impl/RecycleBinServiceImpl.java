package com.lifeviews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifeviews.dto.RecycleBinQueryDTO;
import com.lifeviews.entity.DiaryImage;
import com.lifeviews.entity.DiaryRecord;
import com.lifeviews.enums.RecycleModuleEnum;
import com.lifeviews.mapper.DiaryImageMapper;
import com.lifeviews.mapper.DiaryRecordMapper;
import com.lifeviews.service.RecycleBinService;
import com.lifeviews.vo.RecycleBinItemVO;
import com.lifeviews.vo.RecycleBinPageVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecycleBinServiceImpl implements RecycleBinService {

    private static final int DELETED = 1;
    private static final int NOT_DELETED = 0;
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    private final DiaryRecordMapper diaryRecordMapper;
    private final DiaryImageMapper diaryImageMapper;

    public RecycleBinServiceImpl(DiaryRecordMapper diaryRecordMapper, DiaryImageMapper diaryImageMapper) {
        this.diaryRecordMapper = diaryRecordMapper;
        this.diaryImageMapper = diaryImageMapper;
    }

    @Override
    public RecycleBinPageVO list(Long userId, RecycleBinQueryDTO query) {
        RecycleModuleEnum module = RecycleModuleEnum.fromCode(query.getModule());
        if (module != RecycleModuleEnum.DIARY) {
            return emptyPage();
        }
        return listDiary(userId, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restore(Long userId, String module, Long id) {
        RecycleModuleEnum recycleModule = RecycleModuleEnum.fromCode(module);
        if (recycleModule != RecycleModuleEnum.DIARY) {
            throw notFound();
        }

        int rows = diaryRecordMapper.update(null, new LambdaUpdateWrapper<DiaryRecord>()
                .eq(DiaryRecord::getId, id)
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDeleted, DELETED)
                .set(DiaryRecord::getDeleted, NOT_DELETED)
                .set(DiaryRecord::getDeletedAt, null));
        if (rows == 0) {
            throw notFound();
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, String module, Long id) {
        RecycleModuleEnum recycleModule = RecycleModuleEnum.fromCode(module);
        if (recycleModule != RecycleModuleEnum.DIARY) {
            throw notFound();
        }

        DiaryRecord record = diaryRecordMapper.selectOne(new LambdaQueryWrapper<DiaryRecord>()
                .select(DiaryRecord::getId)
                .eq(DiaryRecord::getId, id)
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDeleted, DELETED));
        if (record == null) {
            throw notFound();
        }

        diaryImageMapper.delete(new LambdaQueryWrapper<DiaryImage>()
                .eq(DiaryImage::getDiaryId, id)
                .eq(DiaryImage::getUserId, userId));
        int rows = diaryRecordMapper.delete(new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getId, id)
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDeleted, DELETED));
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clear(Long userId, String module) {
        RecycleModuleEnum recycleModule = RecycleModuleEnum.fromCode(module);
        if (recycleModule != RecycleModuleEnum.DIARY) {
            return true;
        }

        List<Long> diaryIds = diaryRecordMapper.selectList(new LambdaQueryWrapper<DiaryRecord>()
                        .select(DiaryRecord::getId)
                        .eq(DiaryRecord::getUserId, userId)
                        .eq(DiaryRecord::getDeleted, DELETED))
                .stream()
                .map(DiaryRecord::getId)
                .toList();
        if (diaryIds.isEmpty()) {
            return true;
        }

        diaryImageMapper.delete(new LambdaQueryWrapper<DiaryImage>()
                .eq(DiaryImage::getUserId, userId)
                .in(DiaryImage::getDiaryId, diaryIds));
        diaryRecordMapper.delete(new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDeleted, DELETED));
        return true;
    }

    private RecycleBinPageVO listDiary(Long userId, RecycleBinQueryDTO query) {
        LambdaQueryWrapper<DiaryRecord> wrapper = new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDeleted, DELETED)
                .orderByDesc(DiaryRecord::getDeletedAt)
                .orderByDesc(DiaryRecord::getUpdatedAt);
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(item -> item.like(DiaryRecord::getTitle, keyword)
                    .or()
                    .like(DiaryRecord::getContent, keyword));
        }

        Page<DiaryRecord> page = diaryRecordMapper.selectPage(new Page<>(query.getPage(), query.getPageSize()), wrapper);
        List<DiaryRecord> records = page.getRecords();
        Map<Long, String> coverMap = findDiaryCoverMap(userId, records.stream().map(DiaryRecord::getId).toList());

        RecycleBinPageVO result = new RecycleBinPageVO();
        result.setTotal(page.getTotal());
        result.setItems(records.stream()
                .map(record -> toDiaryRecycleItem(record, coverMap.get(record.getId())))
                .toList());
        return result;
    }

    private Map<Long, String> findDiaryCoverMap(Long userId, List<Long> diaryIds) {
        if (diaryIds == null || diaryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return diaryImageMapper.selectList(new LambdaQueryWrapper<DiaryImage>()
                        .eq(DiaryImage::getUserId, userId)
                        .in(DiaryImage::getDiaryId, diaryIds)
                        .orderByAsc(DiaryImage::getSortOrder)
                        .orderByAsc(DiaryImage::getId))
                .stream()
                .collect(Collectors.groupingBy(DiaryImage::getDiaryId,
                        Collectors.collectingAndThen(Collectors.minBy(Comparator
                                .comparing(DiaryImage::getSortOrder)
                                .thenComparing(DiaryImage::getId)), image -> image.map(DiaryImage::getImageUrl).orElse(null))));
    }

    private RecycleBinItemVO toDiaryRecycleItem(DiaryRecord record, String coverUrl) {
        RecycleBinItemVO vo = new RecycleBinItemVO();
        vo.setId(record.getId());
        vo.setModule(RecycleModuleEnum.DIARY.getCode());
        vo.setTitle(record.getTitle());
        vo.setSummary(summary(record.getContent()));
        vo.setCoverUrl(coverUrl);
        vo.setDeletedAt(record.getDeletedAt());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    private String summary(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String text = HTML_TAG_PATTERN.matcher(content).replaceAll("").replace("&nbsp;", " ").trim();
        int codePoints = text.codePointCount(0, text.length());
        if (codePoints <= 80) {
            return text;
        }
        return text.substring(0, text.offsetByCodePoints(0, 80));
    }

    private RecycleBinPageVO emptyPage() {
        RecycleBinPageVO result = new RecycleBinPageVO();
        result.setTotal(0L);
        result.setItems(Collections.emptyList());
        return result;
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "记录不存在或无权操作");
    }
}
