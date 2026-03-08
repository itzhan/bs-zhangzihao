package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping
    public Result<?> getAll() {
        return Result.success(systemSettingService.getAll());
    }

    @PutMapping
    public Result<?> batchUpdate(@RequestBody Map<String, String> settings) {
        systemSettingService.batchUpdate(settings);
        return Result.success();
    }
}
