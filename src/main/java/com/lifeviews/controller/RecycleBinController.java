package com.lifeviews.controller;

import com.lifeviews.common.Result;
import com.lifeviews.common.UserContext;
import com.lifeviews.dto.RecycleBinQueryDTO;
import com.lifeviews.service.RecycleBinService;
import com.lifeviews.vo.RecycleBinPageVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/recycle-bin")
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    public RecycleBinController(RecycleBinService recycleBinService) {
        this.recycleBinService = recycleBinService;
    }

    @GetMapping
    public Result<RecycleBinPageVO> list(@Valid @ModelAttribute RecycleBinQueryDTO query) {
        return Result.success(recycleBinService.list(UserContext.getCurrentUserId(), query));
    }

    @PutMapping("/{module}/{id}/restore")
    public Result<Boolean> restore(@PathVariable String module, @PathVariable Long id) {
        return Result.success(recycleBinService.restore(UserContext.getCurrentUserId(), module, id));
    }

    @DeleteMapping("/{module}/{id}")
    public Result<Boolean> delete(@PathVariable String module, @PathVariable Long id) {
        return Result.success(recycleBinService.delete(UserContext.getCurrentUserId(), module, id));
    }

    @DeleteMapping("/{module}")
    public Result<Boolean> clear(@PathVariable String module) {
        return Result.success(recycleBinService.clear(UserContext.getCurrentUserId(), module));
    }
}
