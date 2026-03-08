package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ReservationDTO;
import com.farmstay.entity.Reservation;

public interface ReservationService extends IService<Reservation> {

    Reservation create(Long userId, ReservationDTO dto);

    Reservation getById(Long id);

    void cancel(Long id, String reason);

    void confirm(Long id);

    void complete(Long id);

    PageResult<Reservation> listByUserId(Long userId, int page, int size, Integer status);

    PageResult<Reservation> listPage(int page, int size, Integer status, Long farmhouseId);
}
