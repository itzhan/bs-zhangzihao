package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.FarmhousePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private FarmhousePackageService farmhousePackageService;

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(farmhousePackageService.getById(id));
    }
}
