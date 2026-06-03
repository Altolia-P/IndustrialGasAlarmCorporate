package com.niit.industrialgasalarmcorporate.infrastructure.storage;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private static final Set<String> ALLOWED_DOC_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "zip", "rar", "txt", "csv", "jpg", "jpeg", "png"
    );

    private static final Set<String> ALLOWED_DOC_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/zip",
            "application/x-zip-compressed",
            "application/x-rar-compressed",
            "text/plain",
            "text/csv",
            "image/jpeg",
            "image/png"
    );

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:5242880}")
    private long maxSize;

    private Path imagesDir;
    private Path documentsDir;

    @PostConstruct
    void init() throws IOException {
        Path base = Paths.get(uploadPath).toAbsolutePath().normalize();
        imagesDir = base.resolve("images");
        documentsDir = base.resolve("documents");
        Files.createDirectories(imagesDir);
        Files.createDirectories(documentsDir);
        log.info("文件存储目录已就绪: images={}, documents={}", imagesDir.toAbsolutePath(), documentsDir.toAbsolutePath());
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅支持 jpg/png/webp 格式图片");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅支持 jpg/png/webp 格式图片");
            }
        }
        String storedFilename = UUID.randomUUID().toString() + "." + extension;
        Path targetPath = imagesDir.resolve(storedFilename);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("图片已保存: {}", targetPath.toAbsolutePath());
            return "/uploads/images/" + storedFilename;
        } catch (IOException e) {
            log.error("图片保存失败: {}", targetPath.toAbsolutePath(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    public String storeDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOC_MIME_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "不支持的文件类型：" + contentType);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "bin";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_DOC_EXTENSIONS.contains(extension)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                        "不支持的文件类型：" + extension + "，支持：pdf/doc/docx/xls/xlsx/ppt/pptx/zip/txt/csv");
            }
        }
        String storedFilename = UUID.randomUUID().toString() + "." + extension;
        Path targetPath = documentsDir.resolve(storedFilename);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("文档已保存: {}", targetPath.toAbsolutePath());
            return "/uploads/documents/" + storedFilename;
        } catch (IOException e) {
            log.error("文档保存失败: {}", targetPath.toAbsolutePath(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return;
        }
        String relativePath = fileUrl.substring("/uploads/".length());
        Path targetPath = Paths.get(uploadPath, relativePath).normalize();
        Path baseDir = Paths.get(uploadPath).normalize();
        if (!targetPath.startsWith(baseDir)) {
            log.warn("拒绝删除越界路径: {}", fileUrl);
            return;
        }
        try {
            Files.deleteIfExists(targetPath);
            log.debug("文件已删除: {}", targetPath);
        } catch (IOException e) {
            log.warn("文件删除失败: {}", targetPath, e);
        }
    }
}
