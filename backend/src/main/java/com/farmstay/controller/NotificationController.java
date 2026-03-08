package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.NotificationService;
import com.farmstay.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Result<?> listMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(notificationService.listByUserId(userId, page, size));
    }

    @PutMapping("/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<?> markAllRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllRead(userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<?> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(notificationService.getUnreadCount(userId));
    }
}
