package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/statistics")
    public Result<?> getStatistics() {
        return Result.success(dashboardService.getStatistics());
    }

    @GetMapping("/reservation-trend")
    public Result<?> getReservationTrend(
            @RequestParam(defaultValue = "30") int days) {
        return Result.success(dashboardService.getReservationTrend(days));
    }

    @GetMapping("/revenue-trend")
    public Result<?> getRevenueTrend(
            @RequestParam(defaultValue = "30") int days) {
        return Result.success(dashboardService.getRevenueTrend(days));
    }

    @GetMapping("/top-farmhouses")
    public Result<?> getTopFarmhouses(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(dashboardService.getTopFarmhouses(limit));
    }

    @GetMapping("/rating-distribution")
    public Result<?> getRatingDistribution() {
        return Result.success(dashboardService.getRatingDistribution());
    }
}
