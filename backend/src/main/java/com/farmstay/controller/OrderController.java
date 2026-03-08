package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.dto.OrderDTO;
import com.farmstay.service.OrderService;
import com.farmstay.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<?> create(@Valid @RequestBody OrderDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.create(userId, dto));
    }

    @GetMapping
    public Result<?> listMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.listByUserId(userId, page, size, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PutMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id, @RequestBody Map<String, String> params) {
        orderService.pay(id, params.get("paymentMethod"));
        return Result.success("支付成功", null);
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, @RequestBody Map<String, String> params) {
        orderService.cancel(id, params.get("reason"));
        return Result.success("取消成功", null);
    }

    @PutMapping("/{id}/refund")
    public Result<?> requestRefund(@PathVariable Long id, @RequestBody Map<String, String> params) {
        orderService.requestRefund(id, params.get("reason"));
        return Result.success("退款申请已提交", null);
    }
}
