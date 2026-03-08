package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.OrderDTO;
import com.farmstay.entity.Order;

public interface OrderService extends IService<Order> {

    Order create(Long userId, OrderDTO dto);

    Order getById(Long id);

    Order pay(Long id, String paymentMethod);

    Order cancel(Long id, String reason);

    Order requestRefund(Long id, String reason);

    Order processRefund(Long id, boolean approve);

    Order complete(Long id);

    PageResult<Order> listByUserId(Long userId, int page, int size, Integer status);

    PageResult<Order> listPage(int page, int size, Integer status, Long farmhouseId);
}
