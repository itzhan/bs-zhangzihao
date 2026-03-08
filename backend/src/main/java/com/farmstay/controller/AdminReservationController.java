package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long farmhouseId) {
        return Result.success(reservationService.listPage(page, size, status, farmhouseId));
    }

    @PutMapping("/{id}/confirm")
    public Result<?> confirm(@PathVariable Long id) {
        reservationService.confirm(id);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id) {
        reservationService.complete(id);
        return Result.success();
    }
}
