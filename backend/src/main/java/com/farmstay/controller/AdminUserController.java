package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<?> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.listPage(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PutMapping("/{id}/status")
    public Result<?> toggleStatus(@PathVariable Long id) {
        userService.toggleStatus(id);
        return Result.success();
    }
}
