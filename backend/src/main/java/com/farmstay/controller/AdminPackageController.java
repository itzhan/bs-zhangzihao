package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.PackageDTO;
import com.farmstay.service.FarmhousePackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/packages")
public class AdminPackageController {

    @Autowired
    private FarmhousePackageService farmhousePackageService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long farmhouseId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(farmhousePackageService.listPage(page, size, farmhouseId, type, status));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody PackageDTO dto) {
        farmhousePackageService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody PackageDTO dto) {
        farmhousePackageService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        farmhousePackageService.delete(id);
        return Result.success();
    }
}
