package com.farmstay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmstay.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
