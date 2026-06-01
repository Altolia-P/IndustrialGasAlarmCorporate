package com.niit.industrialgasalarmcorporate.interfaces.staff;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffNotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications/unread-count")
    public Result<Long> getUnreadCount(@RequestParam(required = false) String since) {
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
}
