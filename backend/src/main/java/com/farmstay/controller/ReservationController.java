package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.ReservationDTO;
import com.farmstay.service.ReservationService;
import com.farmstay.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public Result<?> create(@Valid @RequestBody ReservationDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(reservationService.create(userId, dto));
    }

    @GetMapping
    public Result<?> listMyReservations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(reservationService.listByUserId(userId, page, size, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(reservationService.getById(id));
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, @RequestBody Map<String, String> params) {
        reservationService.cancel(id, params.get("reason"));
        return Result.success("取消成功", null);
    }
}
