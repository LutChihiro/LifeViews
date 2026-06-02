package com.lifeviews.controller;

import com.lifeviews.common.Result;
import com.lifeviews.common.UserContext;
import com.lifeviews.dto.MovieCreateDTO;
import com.lifeviews.dto.MovieQueryDTO;
import com.lifeviews.dto.MovieUpdateDTO;
import com.lifeviews.service.MovieService;
import com.lifeviews.vo.MoviePageVO;
import com.lifeviews.vo.MovieRecordVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public Result<MovieRecordVO> create(@Valid @RequestBody MovieCreateDTO request) {
        return Result.success(movieService.create(UserContext.getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    public Result<MovieRecordVO> update(@PathVariable Long id, @Valid @RequestBody MovieUpdateDTO request) {
        return Result.success(movieService.update(UserContext.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        movieService.delete(UserContext.getCurrentUserId(), id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MovieRecordVO> detail(@PathVariable Long id) {
        return Result.success(movieService.detail(UserContext.getCurrentUserId(), id));
    }

    @GetMapping
    public Result<MoviePageVO> list(@Valid @ModelAttribute MovieQueryDTO query) {
        return Result.success(movieService.list(UserContext.getCurrentUserId(), query));
    }
}
