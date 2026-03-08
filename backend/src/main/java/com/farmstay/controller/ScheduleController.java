package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/package/{packageId}")
    public Result<?> listByPackage(@PathVariable Long packageId) {
        return Result.success(scheduleService.listByPackageId(packageId));
    }
}
