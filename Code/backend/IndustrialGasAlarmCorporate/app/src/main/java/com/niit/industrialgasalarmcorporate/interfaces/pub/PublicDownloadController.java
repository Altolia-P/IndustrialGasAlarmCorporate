package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.download.service.DownloadFileService;
import com.niit.industrialgasalarmcorporate.application.download.vo.DownloadFileVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicDownloadController {

    private final DownloadFileService downloadFileService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @GetMapping("/downloads")
    public Result<Page<DownloadFileVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.ok(downloadFileService.listFiles(page, size));
    }

    @GetMapping("/downloads/{uuid}/file")
    public ResponseEntity<Resource> download(@PathVariable String uuid) {
        DownloadFileVO vo = downloadFileService.getFile(uuid);
        String storedPath = vo.getStoredPath();
        if (storedPath == null || !storedPath.startsWith("/uploads/")) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "文件路径无效");
        }
        String filename = storedPath.substring("/uploads/".length());
        Path resolved = Paths.get(uploadPath, filename).normalize();
        Path baseDir = Paths.get(uploadPath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "文件路径无效");
        }
        File file = resolved.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "文件不存在");
        }

        FileSystemResource resource = new FileSystemResource(file);
        String encodedName = URLEncoder.encode(vo.getOriginalName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName)
                .body(resource);
    }
}
