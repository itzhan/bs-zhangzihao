package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.ReviewDTO;
import com.farmstay.service.ReviewService;
import com.farmstay.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result<?> create(@Valid @RequestBody ReviewDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewService.create(userId, dto);
        return Result.success();
    }

    @GetMapping("/public/farmhouse/{farmhouseId}")
    public Result<?> listByFarmhouse(
            @PathVariable Long farmhouseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.listApprovedByFarmhouseId(farmhouseId, page, size));
    }
}
