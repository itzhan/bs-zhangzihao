package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ReservationDTO;
import com.farmstay.entity.Reservation;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.ReservationMapper;
import com.farmstay.service.ReservationService;
import com.farmstay.service.ScheduleService;
import com.farmstay.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Autowired
    private ScheduleService scheduleService;

    @Override
    @Transactional
    public Reservation create(Long userId, ReservationDTO dto) {
        // 扣减排期配额
        boolean success = scheduleService.decreaseQuota(dto.getScheduleId(), dto.getPersonCount());
        if (!success) {
            throw new BusinessException("排期名额不足");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationNo("RSV" + System.currentTimeMillis());
        reservation.setUserId(userId);
        reservation.setFarmhouseId(dto.getFarmhouseId());
        reservation.setPackageId(dto.getPackageId());
        reservation.setScheduleId(dto.getScheduleId());
        reservation.setReserveDate(dto.getReserveDate());
        reservation.setPersonCount(dto.getPersonCount());
        reservation.setContactName(dto.getContactName());
        reservation.setContactPhone(dto.getContactPhone());
        reservation.setRemark(dto.getRemark());
        reservation.setStatus(0); // 待确认
        save(reservation);
        return reservation;
    }

    @Override
    public Reservation getById(Long id) {
        Reservation reservation = super.getById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        return reservation;
    }

    @Override
    @Transactional
    public void cancel(Long id, String reason) {
        Reservation reservation = getById(id);
        Long userId = SecurityUtil.getCurrentUserId();
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        if (reservation.getStatus() == 2) {
            throw new BusinessException("预约已取消");
        }
        reservation.setStatus(2); // 已取消
        reservation.setCancelReason(reason);
        updateById(reservation);

        // 恢复排期配额
        scheduleService.decreaseQuota(reservation.getScheduleId(), -reservation.getPersonCount());
    }

    @Override
    public void confirm(Long id) {
        Reservation reservation = getById(id);
        if (reservation.getStatus() != 0) {
            throw new BusinessException("只能确认待确认的预约");
        }
        reservation.setStatus(1); // 已确认
        updateById(reservation);
    }

    @Override
    public void complete(Long id) {
        Reservation reservation = getById(id);
        if (reservation.getStatus() != 1) {
            throw new BusinessException("只能完成已确认的预约");
        }
        reservation.setStatus(3); // 已完成
        updateById(reservation);
    }

    @Override
    public PageResult<Reservation> listByUserId(Long userId, int page, int size, Integer status) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId);
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);

        Page<Reservation> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Reservation> listPage(int page, int size, Integer status, Long farmhouseId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        if (farmhouseId != null) {
            wrapper.eq(Reservation::getFarmhouseId, farmhouseId);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);

        Page<Reservation> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
