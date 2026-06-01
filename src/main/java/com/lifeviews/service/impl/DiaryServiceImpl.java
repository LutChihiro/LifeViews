package com.lifeviews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifeviews.dto.DiaryCreateDTO;
import com.lifeviews.dto.DiaryImageDTO;
import com.lifeviews.dto.DiaryQueryDTO;
import com.lifeviews.dto.DiaryUpdateDTO;
import com.lifeviews.entity.DiaryImage;
import com.lifeviews.entity.DiaryRecord;
import com.lifeviews.mapper.DiaryImageMapper;
import com.lifeviews.mapper.DiaryRecordMapper;
import com.lifeviews.service.DiaryService;
import com.lifeviews.utils.FileStorageUtil;
import com.lifeviews.vo.DiaryDetailVO;
import com.lifeviews.vo.DiaryImageVO;
import com.lifeviews.vo.DiaryListVO;
import com.lifeviews.vo.DiaryUploadVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImpl implements DiaryService {

    private static final int DEFAULT_MOOD = 5;
    private static final String DEFAULT_MOOD_TEXT = "开心";
    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_DELETED = 2;

    private final DiaryRecordMapper diaryRecordMapper;
    private final DiaryImageMapper diaryImageMapper;
    private final FileStorageUtil fileStorageUtil;

    public DiaryServiceImpl(DiaryRecordMapper diaryRecordMapper,
                            DiaryImageMapper diaryImageMapper,
                            FileStorageUtil fileStorageUtil) {
        this.diaryRecordMapper = diaryRecordMapper;
        this.diaryImageMapper = diaryImageMapper;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public Page<DiaryListVO> list(Long userId, DiaryQueryDTO query) {
        Page<DiaryRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DiaryRecord> wrapper = new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getUserId, userId)
                .orderByDesc(DiaryRecord::getIsPinned)
                .orderByDesc(DiaryRecord::getDiaryDate)
                .orderByDesc(DiaryRecord::getUpdatedAt);

        if (query.getStatus() == null) {
            wrapper.in(DiaryRecord::getStatus, STATUS_DRAFT, STATUS_NORMAL);
        } else {
            wrapper.eq(DiaryRecord::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(item -> item.like(DiaryRecord::getTitle, keyword)
                    .or()
                    .like(DiaryRecord::getContent, keyword));
        }
        if (query.getMood() != null) {
            wrapper.eq(DiaryRecord::getMood, query.getMood());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(DiaryRecord::getDiaryDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(DiaryRecord::getDiaryDate, query.getEndDate());
        }

        Page<DiaryRecord> recordPage = diaryRecordMapper.selectPage(page, wrapper);
        List<DiaryRecord> records = recordPage.getRecords();
        Map<Long, List<DiaryImageVO>> imageMap = findImagesByDiaryIds(userId, records.stream()
                .map(DiaryRecord::getId)
                .toList());

        Page<DiaryListVO> result = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        result.setPages(recordPage.getPages());
        result.setRecords(records.stream()
                .map(record -> toListVO(record, imageMap.getOrDefault(record.getId(), Collections.emptyList())))
                .toList());
        return result;
    }

    @Override
    public DiaryDetailVO detail(Long userId, Long id) {
        DiaryRecord record = findOwnedReadableDiary(userId, id);
        return toDetailVO(record, findImagesByDiaryId(userId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryDetailVO create(Long userId, DiaryCreateDTO request) {
        ensureDiaryDateAvailable(userId, null, request);

        LocalDateTime now = LocalDateTime.now();
        DiaryRecord record = new DiaryRecord();
        fillRecord(record, request);
        record.setUserId(userId);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        diaryRecordMapper.insert(record);

        replaceImages(userId, record.getId(), request.getImages());
        return detail(userId, record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryDetailVO update(Long userId, Long id, DiaryUpdateDTO request) {
        DiaryRecord record = findOwnedReadableDiary(userId, id);
        ensureDiaryDateAvailable(userId, id, request);

        fillRecord(record, request);
        record.setUpdatedAt(LocalDateTime.now());
        updateRecord(record);

        replaceImages(userId, id, request.getImages());
        return detail(userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        int rows = diaryRecordMapper.update(null, new LambdaUpdateWrapper<DiaryRecord>()
                .eq(DiaryRecord::getId, id)
                .eq(DiaryRecord::getUserId, userId)
                .ne(DiaryRecord::getStatus, STATUS_DELETED)
                .set(DiaryRecord::getStatus, STATUS_DELETED)
                .set(DiaryRecord::getUpdatedAt, LocalDateTime.now()));
        if (rows == 0) {
            throw new IllegalArgumentException("diary does not exist");
        }
    }

    @Override
    public DiaryUploadVO uploadImage(MultipartFile file) {
        return fileStorageUtil.storeDiaryImage(file);
    }

    private DiaryRecord findOwnedReadableDiary(Long userId, Long id) {
        DiaryRecord record = diaryRecordMapper.selectOne(new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getId, id)
                .eq(DiaryRecord::getUserId, userId)
                .ne(DiaryRecord::getStatus, STATUS_DELETED));
        if (record == null) {
            throw new IllegalArgumentException("diary does not exist");
        }
        return record;
    }

    private void ensureDiaryDateAvailable(Long userId, Long excludeDiaryId, DiaryCreateDTO request) {
        LambdaQueryWrapper<DiaryRecord> wrapper = new LambdaQueryWrapper<DiaryRecord>()
                .eq(DiaryRecord::getUserId, userId)
                .eq(DiaryRecord::getDiaryDate, request.getDiaryDate())
                .ne(DiaryRecord::getStatus, STATUS_DELETED);
        if (excludeDiaryId != null) {
            wrapper.ne(DiaryRecord::getId, excludeDiaryId);
        }
        if (diaryRecordMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("diary already exists for this date");
        }
    }

    private void fillRecord(DiaryRecord record, DiaryCreateDTO request) {
        record.setTitle(request.getTitle().trim());
        record.setContent(StringUtils.hasText(request.getContent()) ? request.getContent() : null);
        record.setDiaryDate(request.getDiaryDate());
        record.setMood(request.getMood() == null ? DEFAULT_MOOD : request.getMood());
        record.setMoodText(StringUtils.hasText(request.getMoodText()) ? request.getMoodText().trim() : DEFAULT_MOOD_TEXT);
        record.setWeather(toNullable(request.getWeather()));
        record.setIsPinned(request.getIsPinned());
        record.setStatus(request.getStatus());
        record.setWordCount(countWords(request.getContent()));
    }

    private void updateRecord(DiaryRecord record) {
        diaryRecordMapper.update(null, new LambdaUpdateWrapper<DiaryRecord>()
                .eq(DiaryRecord::getId, record.getId())
                .eq(DiaryRecord::getUserId, record.getUserId())
                .ne(DiaryRecord::getStatus, STATUS_DELETED)
                .set(DiaryRecord::getTitle, record.getTitle())
                .set(DiaryRecord::getContent, record.getContent())
                .set(DiaryRecord::getDiaryDate, record.getDiaryDate())
                .set(DiaryRecord::getMood, record.getMood())
                .set(DiaryRecord::getMoodText, record.getMoodText())
                .set(DiaryRecord::getWeather, record.getWeather())
                .set(DiaryRecord::getIsPinned, record.getIsPinned())
                .set(DiaryRecord::getWordCount, record.getWordCount())
                .set(DiaryRecord::getStatus, record.getStatus())
                .set(DiaryRecord::getUpdatedAt, record.getUpdatedAt()));
    }

    private int countWords(String content) {
        if (!StringUtils.hasText(content)) {
            return 0;
        }
        String text = content.replaceAll("\\s+", "");
        return text.codePointCount(0, text.length());
    }

    private void replaceImages(Long userId, Long diaryId, List<DiaryImageDTO> images) {
        diaryImageMapper.delete(new LambdaQueryWrapper<DiaryImage>()
                .eq(DiaryImage::getDiaryId, diaryId)
                .eq(DiaryImage::getUserId, userId));

        if (images == null || images.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<DiaryImage> imageEntities = new ArrayList<>();
        for (DiaryImageDTO image : images) {
            DiaryImage entity = new DiaryImage();
            entity.setDiaryId(diaryId);
            entity.setUserId(userId);
            entity.setImageUrl(image.getImageUrl());
            entity.setImageName(toNullable(image.getImageName()));
            entity.setSortOrder(image.getSortOrder() == null ? 0 : image.getSortOrder());
            entity.setCreatedAt(now);
            imageEntities.add(entity);
        }
        imageEntities.forEach(diaryImageMapper::insert);
    }

    private Map<Long, List<DiaryImageVO>> findImagesByDiaryIds(Long userId, List<Long> diaryIds) {
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
                        Collectors.collectingAndThen(Collectors.toList(), images -> images.stream()
                                .limit(3)
                                .map(this::toImageVO)
                                .toList())));
    }

    private List<DiaryImageVO> findImagesByDiaryId(Long userId, Long diaryId) {
        return diaryImageMapper.selectList(new LambdaQueryWrapper<DiaryImage>()
                        .eq(DiaryImage::getUserId, userId)
                        .eq(DiaryImage::getDiaryId, diaryId)
                        .orderByAsc(DiaryImage::getSortOrder)
                        .orderByAsc(DiaryImage::getId))
                .stream()
                .map(this::toImageVO)
                .toList();
    }

    private DiaryListVO toListVO(DiaryRecord record, List<DiaryImageVO> images) {
        DiaryListVO vo = new DiaryListVO();
        vo.setId(record.getId());
        vo.setTitle(record.getTitle());
        vo.setContentSummary(summary(record.getContent()));
        vo.setDiaryDate(record.getDiaryDate());
        vo.setMood(record.getMood());
        vo.setMoodText(record.getMoodText());
        vo.setWeather(record.getWeather());
        vo.setIsPinned(record.getIsPinned());
        vo.setWordCount(record.getWordCount());
        vo.setStatus(record.getStatus());
        vo.setUpdatedAt(record.getUpdatedAt());
        vo.setImages(images);
        return vo;
    }

    private DiaryDetailVO toDetailVO(DiaryRecord record, List<DiaryImageVO> images) {
        DiaryDetailVO vo = new DiaryDetailVO();
        vo.setId(record.getId());
        vo.setTitle(record.getTitle());
        vo.setContent(record.getContent());
        vo.setDiaryDate(record.getDiaryDate());
        vo.setMood(record.getMood());
        vo.setMoodText(record.getMoodText());
        vo.setWeather(record.getWeather());
        vo.setIsPinned(record.getIsPinned());
        vo.setWordCount(record.getWordCount());
        vo.setStatus(record.getStatus());
        vo.setCreatedAt(record.getCreatedAt());
        vo.setUpdatedAt(record.getUpdatedAt());
        vo.setImages(images);
        return vo;
    }

    private DiaryImageVO toImageVO(DiaryImage image) {
        DiaryImageVO vo = new DiaryImageVO();
        vo.setId(image.getId());
        vo.setImageUrl(image.getImageUrl());
        vo.setImageName(image.getImageName());
        vo.setSortOrder(image.getSortOrder());
        return vo;
    }

    private String summary(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String text = content.trim();
        int codePoints = text.codePointCount(0, text.length());
        if (codePoints <= 100) {
            return text;
        }
        return text.substring(0, text.offsetByCodePoints(0, 100));
    }

    private String toNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
