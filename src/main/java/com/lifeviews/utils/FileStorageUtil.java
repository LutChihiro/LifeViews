package com.lifeviews.utils;

import com.lifeviews.vo.DiaryUploadVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Path DIARY_UPLOAD_DIR = Paths.get("uploads", "diary");

    public DiaryUploadVO storeDiaryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file cannot be empty");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("image size cannot exceed 5MB");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("only jpg, jpeg, png and webp images are allowed");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("only jpg, jpeg, png and webp images are allowed");
        }

        try {
            Files.createDirectories(DIARY_UPLOAD_DIR);
            String storedFilename = UUID.randomUUID() + "." + extension;
            Path targetPath = DIARY_UPLOAD_DIR.resolve(storedFilename).normalize();
            file.transferTo(targetPath);

            DiaryUploadVO vo = new DiaryUploadVO();
            vo.setImageUrl("/uploads/diary/" + storedFilename);
            vo.setImageName(originalFilename);
            return vo;
        } catch (IOException ex) {
            throw new IllegalStateException("failed to store image", ex);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
