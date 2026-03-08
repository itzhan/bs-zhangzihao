package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.LoginRequest;
import com.farmstay.dto.RegisterRequest;
import com.farmstay.service.UserService;
import com.farmstay.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    @GetMapping("/info")
    public Result<?> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getUserById(userId));
    }
}
