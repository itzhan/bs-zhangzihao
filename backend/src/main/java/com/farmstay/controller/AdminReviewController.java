package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.ReviewReplyDTO;
import com.farmstay.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long farmhouseId) {
        return Result.success(reviewService.listPage(page, size, status, farmhouseId));
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id) {
        reviewService.approve(id);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id) {
        reviewService.reject(id);
        return Result.success();
    }

    @PutMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Long id, @Valid @RequestBody ReviewReplyDTO dto) {
        reviewService.reply(id, dto);
        return Result.success();
    }
}
