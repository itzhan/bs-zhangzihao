package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long farmhouseId) {
        return Result.success(orderService.listPage(page, size, status, farmhouseId));
    }

    @PutMapping("/{id}/refund")
    public Result<?> processRefund(@PathVariable Long id, @RequestBody Map<String, Boolean> params) {
        orderService.processRefund(id, params.get("approve"));
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
