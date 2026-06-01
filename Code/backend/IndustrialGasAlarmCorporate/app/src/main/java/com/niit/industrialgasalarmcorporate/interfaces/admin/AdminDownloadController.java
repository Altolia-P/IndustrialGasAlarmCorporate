package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.download.service.DownloadFileService;
import com.niit.industrialgasalarmcorporate.application.download.vo.DownloadFileVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDownloadController {

    private final DownloadFileService downloadFileService;

    @GetMapping("/downloads")
    public Result<Page<DownloadFileVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(downloadFileService.listFiles(page, size));
    }

    @PostMapping("/downloads")
    public Result<DownloadFileVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String displayName) {
        return Result.ok(downloadFileService.uploadFile(file, displayName));
    }

    @DeleteMapping("/downloads/{uuid}")
    public Result<Void> delete(@PathVariable String uuid) {
        downloadFileService.deleteFile(uuid);
        return Result.ok("删除成功", null);
    }
}
