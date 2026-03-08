package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.UserDTO;
import com.farmstay.service.NotificationService;
import com.farmstay.service.UserService;
import com.farmstay.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/me")
    public Result<?> getProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getUserById(userId));
    }

    @PutMapping("/me")
    public Result<?> updateProfile(@Valid @RequestBody UserDTO userDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.updateProfile(userId, userDTO);
        return Result.success();
    }

    @PutMapping("/me/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.updatePassword(userId, params.get("oldPassword"), params.get("newPassword"));
        return Result.success("密码修改成功", null);
    }

    @GetMapping("/me/notifications")
    public Result<?> listMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(notificationService.listByUserId(userId, page, size));
    }

    @GetMapping("/me/notifications/unread-count")
    public Result<?> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(notificationService.getUnreadCount(userId));
    }
}
