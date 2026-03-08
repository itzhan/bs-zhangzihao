package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.OrderDTO;
import com.farmstay.entity.FarmhousePackage;
import com.farmstay.entity.Order;
import com.farmstay.entity.Payment;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.FarmhousePackageMapper;
import com.farmstay.mapper.OrderMapper;
import com.farmstay.mapper.PaymentMapper;
import com.farmstay.mapper.ScheduleMapper;
import com.farmstay.service.OrderService;
import com.farmstay.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private FarmhousePackageMapper farmhousePackageMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public Order create(Long userId, OrderDTO dto) {
        // 查询套餐信息计算总价
        FarmhousePackage pkg = farmhousePackageMapper.selectById(dto.getPackageId());
        if (pkg == null) {
            throw new BusinessException("套餐不存在");
        }

        BigDecimal totalAmount = pkg.getPrice().multiply(BigDecimal.valueOf(dto.getPersonCount()));

        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setReservationId(dto.getReservationId());
        order.setFarmhouseId(dto.getFarmhouseId());
        order.setPackageId(dto.getPackageId());
        order.setPackageName(pkg.getName());
        order.setPersonCount(dto.getPersonCount());
        order.setUnitPrice(pkg.getPrice());
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待支付
        order.setRemark(dto.getRemark());
        save(order);
        return order;
    }

    @Override
    public Order getById(Long id) {
        Order order = super.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    @Override
    @Transactional
    public Order pay(Long id, String paymentMethod) {
        Order order = getById(id);
        Long userId = SecurityUtil.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        // 更新订单状态
        order.setStatus(1); // 已支付
        order.setPaymentMethod(paymentMethod);
        order.setPayTime(LocalDateTime.now());
        updateById(order);

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionNo("PAY" + System.currentTimeMillis());
        payment.setStatus(1); // 支付成功
        payment.setPayTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        return order;
    }

    @Override
    public Order cancel(Long id, String reason) {
        Order order = getById(id);
        Long userId = SecurityUtil.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待支付的订单才能取消");
        }

        order.setStatus(2); // 已取消
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    @Override
    public Order requestRefund(Long id, String reason) {
        Order order = getById(id);
        Long userId = SecurityUtil.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能申请退款");
        }

        order.setStatus(3); // 退款中
        order.setCancelReason(reason);
        updateById(order);
        return order;
    }

    @Override
    @Transactional
    public Order processRefund(Long id, boolean approve) {
        Order order = getById(id);
        if (order.getStatus() != 3) {
            throw new BusinessException("订单不在退款申请状态");
        }

        if (approve) {
            order.setStatus(4); // 已退款
            // 更新支付记录退款信息
            LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Payment::getOrderId, order.getId());
            Payment payment = paymentMapper.selectOne(wrapper);
            if (payment != null) {
                payment.setStatus(2); // 已退款
                payment.setRefundTime(LocalDateTime.now());
                payment.setRefundAmount(order.getTotalAmount());
                paymentMapper.updateById(payment);
            }
        } else {
            order.setStatus(1); // 退回已支付状态
        }
        updateById(order);
        return order;
    }

    @Override
    public Order complete(Long id) {
        Order order = getById(id);
        if (order.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能完成");
        }
        order.setStatus(5); // 已完成
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    @Override
    public PageResult<Order> listByUserId(Long userId, int page, int size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Order> listPage(int page, int size, Integer status, Long farmhouseId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (farmhouseId != null) {
            wrapper.eq(Order::getFarmhouseId, farmhouseId);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
