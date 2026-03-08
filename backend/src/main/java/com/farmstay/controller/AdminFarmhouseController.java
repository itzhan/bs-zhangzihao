package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.FarmhouseDTO;
import com.farmstay.service.FarmhouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/farmhouses")
public class AdminFarmhouseController {

    @Autowired
    private FarmhouseService farmhouseService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(farmhouseService.listPage(page, size, keyword, status));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody FarmhouseDTO dto) {
        farmhouseService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody FarmhouseDTO dto) {
        farmhouseService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        farmhouseService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> toggleStatus(@PathVariable Long id) {
        farmhouseService.toggleStatus(id);
        return Result.success();
    }
}
