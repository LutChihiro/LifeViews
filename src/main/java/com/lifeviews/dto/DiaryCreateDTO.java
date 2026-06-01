package com.lifeviews.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DiaryCreateDTO {

    @NotBlank(message = "title cannot be blank")
    @Size(max = 255, message = "title length cannot exceed 255")
    private String title;

    private String content;

    @NotNull(message = "diaryDate cannot be null")
    private LocalDate diaryDate;

    @Min(value = 1, message = "mood must be between 1 and 5")
    @Max(value = 5, message = "mood must be between 1 and 5")
    private Integer mood;

    @Size(max = 64, message = "moodText length cannot exceed 64")
    private String moodText;

    @Size(max = 64, message = "weather length cannot exceed 64")
    private String weather;

    @NotNull(message = "isPinned cannot be null")
    @Min(value = 0, message = "isPinned must be 0 or 1")
    @Max(value = 1, message = "isPinned must be 0 or 1")
    private Integer isPinned;

    @NotNull(message = "status cannot be null")
    @Min(value = 0, message = "status must be 0 or 1")
    @Max(value = 1, message = "status must be 0 or 1")
    private Integer status;

    @Valid
    private List<DiaryImageDTO> images;
}
