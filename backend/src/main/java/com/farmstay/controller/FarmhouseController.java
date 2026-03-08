package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.FarmhouseService;
import com.farmstay.service.FarmhousePackageService;
import com.farmstay.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmhouses")
public class FarmhouseController {

    @Autowired
    private FarmhouseService farmhouseService;

    @Autowired
    private FarmhousePackageService farmhousePackageService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public Result<?> listFarmhouses(@RequestParam(required = false) String keyword) {
        return Result.success(farmhouseService.listActive(keyword));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(farmhouseService.getById(id));
    }

    @GetMapping("/{id}/packages")
    public Result<?> listPackages(@PathVariable Long id) {
        return Result.success(farmhousePackageService.listByFarmhouseId(id));
    }

    @GetMapping("/{id}/reviews")
    public Result<?> listReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.listApprovedByFarmhouseId(id, page, size));
    }
}
