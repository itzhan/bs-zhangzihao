package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmstay.entity.*;
import com.farmstay.mapper.*;
import com.farmstay.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FarmhouseMapper farmhouseMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 总用户数
        stats.put("totalUsers", userMapper.selectCount(null));

        // 总农家乐数
        stats.put("totalFarmhouses", farmhouseMapper.selectCount(null));

        // 总预约数
        stats.put("totalReservations", reservationMapper.selectCount(null));

        // 总订单数
        stats.put("totalOrders", orderMapper.selectCount(null));

        // 总收入（已支付 + 已完成订单）
        LambdaQueryWrapper<Order> revenueWrapper = new LambdaQueryWrapper<>();
        revenueWrapper.in(Order::getStatus, 1, 5);
        List<Order> paidOrders = orderMapper.selectList(revenueWrapper);
        BigDecimal totalRevenue = paidOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.ge(User::getCreateTime, todayStart);
        stats.put("todayNewUsers", userMapper.selectCount(todayUserWrapper));

        // 今日新增订单
        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.ge(Order::getCreateTime, todayStart);
        stats.put("todayNewOrders", orderMapper.selectCount(todayOrderWrapper));

        // 今日新增预约
        LambdaQueryWrapper<Reservation> todayReservationWrapper = new LambdaQueryWrapper<>();
        todayReservationWrapper.ge(Reservation::getCreateTime, todayStart);
        stats.put("todayNewReservations", reservationMapper.selectCount(todayReservationWrapper));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getReservationTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Reservation::getCreateTime, start)
                    .le(Reservation::getCreateTime, end);
            long count = reservationMapper.selectCount(wrapper);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    @Override
    public List<Map<String, Object>> getRevenueTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Order::getPayTime, start)
                    .le(Order::getPayTime, end)
                    .in(Order::getStatus, 1, 5);
            List<Order> orders = orderMapper.selectList(wrapper);
            BigDecimal revenue = orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            item.put("revenue", revenue);
            trend.add(item);
        }
        return trend;
    }

    @Override
    public List<Map<String, Object>> getTopFarmhouses(int limit) {
        // 按订单数统计热门农家乐
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getStatus, 1, 3, 5); // 已支付、退款中、已完成
        List<Order> orders = orderMapper.selectList(wrapper);

        Map<Long, Long> farmhouseOrderCount = orders.stream()
                .filter(o -> o.getFarmhouseId() != null)
                .collect(Collectors.groupingBy(Order::getFarmhouseId, Collectors.counting()));

        List<Map.Entry<Long, Long>> sorted = farmhouseOrderCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : sorted) {
            Farmhouse farmhouse = farmhouseMapper.selectById(entry.getKey());
            if (farmhouse != null) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("farmhouseId", farmhouse.getId());
                item.put("farmhouseName", farmhouse.getName());
                item.put("orderCount", entry.getValue());
                item.put("rating", farmhouse.getRating());
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRatingDistribution() {
        // 评分分布统计（1-5分各有多少条评价）
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStatus, 1); // 只统计已审核通过的评价
        List<Review> reviews = reviewMapper.selectList(wrapper);

        Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int rating = 1; rating <= 5; rating++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rating", rating);
            item.put("count", distribution.getOrDefault(rating, 0L));
            result.add(item);
        }
        return result;
    }
}
