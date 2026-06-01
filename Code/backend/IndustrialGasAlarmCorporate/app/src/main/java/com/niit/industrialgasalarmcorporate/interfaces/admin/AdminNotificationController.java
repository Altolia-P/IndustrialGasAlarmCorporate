package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public Result<Page<NotificationVO>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(notificationService.listAll(page, size));
    }

    @GetMapping("/notifications/unread-count")
    public Result<Long> getUnreadCount(
            @RequestParam(required = false) String since) {
        LocalDateTime sinceTime;
        if (since != null && !since.isBlank()) {
            sinceTime = LocalDateTime.parse(since, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            sinceTime = LocalDateTime.now().minusHours(24);
        }
        return Result.ok(notificationService.getUnreadCount(sinceTime));
    }

    @GetMapping("/notifications/recent")
    public Result<List<NotificationVO>> getRecent() {
        return Result.ok(notificationService.getRecent(5));
    }

    @GetMapping("/alerts/{uuid}/notifications")
    public Result<Page<NotificationVO>> getByAlert(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(notificationService.findByAlertUuid(uuid, page, size));
    }

    @PostMapping("/notifications/{uuid}/resend")
    public Result<Void> resend(@PathVariable String uuid) {
        notificationService.resend(uuid);
        return Result.ok("重发成功", null);
    }
}
