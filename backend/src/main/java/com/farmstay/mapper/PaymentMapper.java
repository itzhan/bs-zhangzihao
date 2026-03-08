package com.farmstay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmstay.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
