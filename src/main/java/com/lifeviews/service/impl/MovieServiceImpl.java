package com.lifeviews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifeviews.dto.MovieCreateDTO;
import com.lifeviews.dto.MovieQueryDTO;
import com.lifeviews.dto.MovieUpdateDTO;
import com.lifeviews.entity.MovieRecord;
import com.lifeviews.mapper.MovieRecordMapper;
import com.lifeviews.service.MovieService;
import com.lifeviews.vo.MoviePageVO;
import com.lifeviews.vo.MovieRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class MovieServiceImpl implements MovieService {

    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_DELETED = 2;
    private static final int WATCH_STATUS_WANTED = 0;
    private static final int WATCH_STATUS_WATCHED = 1;

    private final MovieRecordMapper movieRecordMapper;

    public MovieServiceImpl(MovieRecordMapper movieRecordMapper) {
        this.movieRecordMapper = movieRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieRecordVO create(Long userId, MovieCreateDTO request) {
        LocalDateTime now = LocalDateTime.now();
        MovieRecord record = new MovieRecord();
        fillRecord(record, request);
        record.setUserId(userId);
        record.setStatus(STATUS_NORMAL);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        movieRecordMapper.insert(record);
        return detail(userId, record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieRecordVO update(Long userId, Long id, MovieUpdateDTO request) {
        findOwnedNormalRecord(userId, id);

        MovieRecord record = new MovieRecord();
        fillRecord(record, request);
        record.setId(id);
        record.setUserId(userId);
        record.setUpdatedAt(LocalDateTime.now());

        int rows = movieRecordMapper.update(null, new LambdaUpdateWrapper<MovieRecord>()
                .eq(MovieRecord::getId, id)
                .eq(MovieRecord::getUserId, userId)
                .eq(MovieRecord::getStatus, STATUS_NORMAL)
                .set(MovieRecord::getMovieName, record.getMovieName())
                .set(MovieRecord::getPosterUrl, record.getPosterUrl())
                .set(MovieRecord::getCategory, record.getCategory())
                .set(MovieRecord::getRegion, record.getRegion())
                .set(MovieRecord::getReleaseYear, record.getReleaseYear())
                .set(MovieRecord::getWatchDate, record.getWatchDate())
                .set(MovieRecord::getWatchPlace, record.getWatchPlace())
                .set(MovieRecord::getRating, record.getRating())
                .set(MovieRecord::getReview, record.getReview())
                .set(MovieRecord::getWatchStatus, record.getWatchStatus())
                .set(MovieRecord::getStatus, STATUS_NORMAL)
                .set(MovieRecord::getUpdatedAt, record.getUpdatedAt()));
        if (rows == 0) {
            throw new IllegalArgumentException("movie record does not exist");
        }
        return detail(userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        int rows = movieRecordMapper.update(null, new LambdaUpdateWrapper<MovieRecord>()
                .eq(MovieRecord::getId, id)
                .eq(MovieRecord::getUserId, userId)
                .eq(MovieRecord::getStatus, STATUS_NORMAL)
                .set(MovieRecord::getStatus, STATUS_DELETED)
                .set(MovieRecord::getUpdatedAt, LocalDateTime.now()));
        if (rows == 0) {
            throw new IllegalArgumentException("movie record does not exist");
        }
    }

    @Override
    public MovieRecordVO detail(Long userId, Long id) {
        return toVO(findOwnedNormalRecord(userId, id));
    }

    @Override
    public MoviePageVO list(Long userId, MovieQueryDTO query) {
        Page<MovieRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<MovieRecord> wrapper = new LambdaQueryWrapper<MovieRecord>()
                .eq(MovieRecord::getUserId, userId)
                .eq(MovieRecord::getStatus, STATUS_NORMAL)
                .orderByDesc(MovieRecord::getUpdatedAt)
                .orderByDesc(MovieRecord::getId);

        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(item -> item.like(MovieRecord::getMovieName, keyword)
                    .or()
                    .like(MovieRecord::getReview, keyword));
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(MovieRecord::getCategory, query.getCategory().trim());
        }
        if (StringUtils.hasText(query.getRegion())) {
            wrapper.eq(MovieRecord::getRegion, query.getRegion().trim());
        }
        if (query.getWatchStatus() != null) {
            wrapper.eq(MovieRecord::getWatchStatus, query.getWatchStatus());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(MovieRecord::getWatchDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(MovieRecord::getWatchDate, query.getEndDate());
        }
        if (query.getMinRating() != null) {
            wrapper.ge(MovieRecord::getRating, normalizeRating(query.getMinRating()));
        }
        if (query.getMaxRating() != null) {
            wrapper.le(MovieRecord::getRating, normalizeRating(query.getMaxRating()));
        }

        Page<MovieRecord> recordPage = movieRecordMapper.selectPage(page, wrapper);
        MoviePageVO result = new MoviePageVO();
        result.setTotal(recordPage.getTotal());
        result.setItems(recordPage.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    private MovieRecord findOwnedNormalRecord(Long userId, Long id) {
        MovieRecord record = movieRecordMapper.selectOne(new LambdaQueryWrapper<MovieRecord>()
                .eq(MovieRecord::getId, id)
                .eq(MovieRecord::getUserId, userId)
                .eq(MovieRecord::getStatus, STATUS_NORMAL));
        if (record == null) {
            throw new IllegalArgumentException("movie record does not exist");
        }
        return record;
    }

    private void fillRecord(MovieRecord record, MovieCreateDTO request) {
        record.setMovieName(request.getMovieName().trim());
        record.setPosterUrl(toNullable(request.getPosterUrl()));
        record.setCategory(toNullable(request.getCategory()));
        record.setRegion(toNullable(request.getRegion()));
        record.setReleaseYear(request.getReleaseYear());
        record.setWatchDate(request.getWatchDate());
        record.setWatchPlace(toNullable(request.getWatchPlace()));
        record.setRating(request.getRating() == null ? null : normalizeRating(request.getRating()));
        record.setReview(StringUtils.hasText(request.getReview()) ? request.getReview() : null);
        record.setWatchStatus(request.getWatchStatus() == null ? WATCH_STATUS_WATCHED : request.getWatchStatus());
        record.setStatus(STATUS_NORMAL);
    }

    private BigDecimal normalizeRating(BigDecimal rating) {
        return rating.setScale(1, RoundingMode.HALF_UP);
    }

    private String toNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private MovieRecordVO toVO(MovieRecord record) {
        MovieRecordVO vo = new MovieRecordVO();
        vo.setId(record.getId());
        vo.setMovieName(record.getMovieName());
        vo.setPosterUrl(record.getPosterUrl());
        vo.setCategory(record.getCategory());
        vo.setRegion(record.getRegion());
        vo.setReleaseYear(record.getReleaseYear());
        vo.setWatchDate(record.getWatchDate());
        vo.setWatchPlace(record.getWatchPlace());
        vo.setRating(record.getRating());
        vo.setReview(record.getReview());
        vo.setWatchStatus(record.getWatchStatus());
        vo.setStatus(record.getStatus());
        vo.setCreatedAt(record.getCreatedAt());
        vo.setUpdatedAt(record.getUpdatedAt());
        return vo;
    }
}
