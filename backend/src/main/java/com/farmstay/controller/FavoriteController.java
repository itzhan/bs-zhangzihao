package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.FavoriteService;
import com.farmstay.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/{farmhouseId}")
    public Result<?> add(@PathVariable Long farmhouseId) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriteService.add(userId, farmhouseId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/{farmhouseId}")
    public Result<?> remove(@PathVariable Long farmhouseId) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriteService.remove(userId, farmhouseId);
        return Result.success("取消收藏成功", null);
    }

    @GetMapping
    public Result<?> listMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(favoriteService.listFarmhousesByUserId(userId, page, size));
    }

    @GetMapping("/check/{farmhouseId}")
    public Result<?> isFavorited(@PathVariable Long farmhouseId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(favoriteService.isFavorited(userId, farmhouseId));
    }
}
