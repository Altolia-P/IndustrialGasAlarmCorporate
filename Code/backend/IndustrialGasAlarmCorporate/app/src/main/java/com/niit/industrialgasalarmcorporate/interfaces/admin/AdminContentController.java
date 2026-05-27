package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.content.dto.CreateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.dto.UpdateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.service.ContentService;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminContentController {

    private final ContentService contentService;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/contents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ContentVO> createContent(
            @RequestParam String title,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String body,
            @RequestParam String type,
            @RequestParam String categoryUuid,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) String status) {
        CreateContentDTO dto = new CreateContentDTO();
        dto.setTitle(title);
        dto.setSummary(summary);
        dto.setBody(body);
        dto.setType(type);
        dto.setCategoryUuid(categoryUuid);
        dto.setStatus(status);
        if (coverImage != null && !coverImage.isEmpty()) {
            dto.setCoverImage(fileStorageService.store(coverImage));
        }
        return Result.ok("新增成功", contentService.createContent(dto));
    }

    @GetMapping("/contents")
    public Result<Page<ContentVO>> getContents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(contentService.findAdminContents(title, type, categoryUuid, status, page, size));
    }

    @PutMapping(value = "/contents/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ContentVO> updateContent(
            @PathVariable String uuid,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String body,
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) String status) {
        UpdateContentDTO dto = new UpdateContentDTO();
        if (title != null) dto.setTitle(title);
        if (summary != null) dto.setSummary(summary);
        if (body != null) dto.setBody(body);
        if (categoryUuid != null) dto.setCategoryUuid(categoryUuid);
        if (status != null) dto.setStatus(status);
        if (coverImage != null && !coverImage.isEmpty()) {
            dto.setCoverImage(fileStorageService.store(coverImage));
        }
        return Result.ok("修改成功", contentService.updateContent(uuid, dto));
    }

    @DeleteMapping("/contents/{uuid}")
    public Result<Void> deleteContent(@PathVariable String uuid) {
        contentService.deleteContent(uuid);
        return Result.ok("删除成功", null);
    }

    @PostMapping("/contents/{uuid}/publish")
    public Result<Void> publishContent(@PathVariable String uuid) {
        contentService.publishContent(uuid);
        return Result.ok("发布成功", null);
    }

    @PostMapping("/contents/{uuid}/unpublish")
    public Result<Void> unpublishContent(@PathVariable String uuid) {
        contentService.unpublishContent(uuid);
        return Result.ok("取消发布成功", null);
    }
}
