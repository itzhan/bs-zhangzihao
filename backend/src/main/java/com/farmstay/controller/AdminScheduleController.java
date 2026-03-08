package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.ScheduleDTO;
import com.farmstay.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/schedules")
public class AdminScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long packageId,
            @RequestParam(required = false) Long farmhouseId) {
        return Result.success(scheduleService.listPage(page, size, packageId, farmhouseId));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody ScheduleDTO dto) {
        scheduleService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ScheduleDTO dto) {
        scheduleService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success();
    }
}
