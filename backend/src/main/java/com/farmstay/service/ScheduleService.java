package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ScheduleDTO;
import com.farmstay.entity.Schedule;

import java.util.List;

public interface ScheduleService extends IService<Schedule> {

    Schedule create(ScheduleDTO dto);

    Schedule update(Long id, ScheduleDTO dto);

    void delete(Long id);

    Schedule getById(Long id);

    List<Schedule> listByPackageId(Long packageId);

    PageResult<Schedule> listPage(int page, int size, Long packageId, Long farmhouseId);

    boolean decreaseQuota(Long id, int count);
}
