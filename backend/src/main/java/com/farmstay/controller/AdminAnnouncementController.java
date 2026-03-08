package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.AnnouncementDTO;
import com.farmstay.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(announcementService.listPage(page, size, type, status));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody AnnouncementDTO dto) {
        announcementService.create(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody AnnouncementDTO dto) {
        announcementService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }
}
