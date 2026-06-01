package com.lifeviews.controller;

import com.lifeviews.common.Result;
import com.lifeviews.common.UserContext;
import com.lifeviews.dto.DiaryCreateDTO;
import com.lifeviews.dto.DiaryQueryDTO;
import com.lifeviews.dto.DiaryUpdateDTO;
import com.lifeviews.service.DiaryService;
import com.lifeviews.vo.DiaryDetailVO;
import com.lifeviews.vo.DiaryPageVO;
import com.lifeviews.vo.DiaryUploadVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping
    public Result<DiaryPageVO> list(@Valid @ModelAttribute DiaryQueryDTO query) {
        return Result.success(diaryService.list(UserContext.getCurrentUserId(), query));
    }

    @GetMapping("/list")
    public Result<DiaryPageVO> listCompat(@Valid @ModelAttribute DiaryQueryDTO query) {
        return Result.success(diaryService.list(UserContext.getCurrentUserId(), query));
    }

    @GetMapping("/{id}")
    public Result<DiaryDetailVO> detail(@PathVariable Long id) {
        return Result.success(diaryService.detail(UserContext.getCurrentUserId(), id));
    }

    @PostMapping
    public Result<DiaryDetailVO> create(@Valid @RequestBody DiaryCreateDTO request) {
        return Result.success(diaryService.create(UserContext.getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    public Result<DiaryDetailVO> update(@PathVariable Long id, @Valid @RequestBody DiaryUpdateDTO request) {
        return Result.success(diaryService.update(UserContext.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        diaryService.delete(UserContext.getCurrentUserId(), id);
        return Result.success();
    }

    @PostMapping("/image")
    public Result<DiaryUploadVO> uploadImage(@NotNull(message = "file cannot be null") @RequestPart("file") MultipartFile file) {
        return Result.success(diaryService.uploadImage(file));
    }
}
