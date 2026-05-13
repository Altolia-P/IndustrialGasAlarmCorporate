package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.content.service.ContentService;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentDetailVO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/contents")
    public Result<Page<ContentVO>> getContents(
            @RequestParam String type,
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(contentService.findPublicContents(type, categoryUuid, page, size));
    }

    @GetMapping("/contents/{uuid}")
    public Result<ContentDetailVO> getContent(@PathVariable String uuid) {
        return Result.ok(contentService.getContent(uuid));
    }
}
