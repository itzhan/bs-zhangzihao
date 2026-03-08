package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ScheduleDTO;
import com.farmstay.entity.Schedule;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.ScheduleMapper;
import com.farmstay.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Override
    public Schedule create(ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        schedule.setPackageId(dto.getPackageId());
        schedule.setFarmhouseId(dto.getFarmhouseId());
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setTotalQuota(dto.getTotalQuota());
        schedule.setRemainingQuota(dto.getRemainingQuota() != null ? dto.getRemainingQuota() : dto.getTotalQuota());
        schedule.setPriceOverride(dto.getPriceOverride());
        schedule.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        save(schedule);
        return schedule;
    }

    @Override
    public Schedule update(Long id, ScheduleDTO dto) {
        Schedule schedule = getById(id);
        if (dto.getPackageId() != null) schedule.setPackageId(dto.getPackageId());
        if (dto.getFarmhouseId() != null) schedule.setFarmhouseId(dto.getFarmhouseId());
        if (dto.getScheduleDate() != null) schedule.setScheduleDate(dto.getScheduleDate());
        if (dto.getTotalQuota() != null) schedule.setTotalQuota(dto.getTotalQuota());
        if (dto.getRemainingQuota() != null) schedule.setRemainingQuota(dto.getRemainingQuota());
        if (dto.getPriceOverride() != null) schedule.setPriceOverride(dto.getPriceOverride());
        if (dto.getStatus() != null) schedule.setStatus(dto.getStatus());
        updateById(schedule);
        return schedule;
    }

    @Override
    public void delete(Long id) {
        Schedule schedule = super.getById(id);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }
        removeById(id);
    }

    @Override
    public Schedule getById(Long id) {
        Schedule schedule = super.getById(id);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }
        return schedule;
    }

    @Override
    public List<Schedule> listByPackageId(Long packageId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getPackageId, packageId)
                .eq(Schedule::getStatus, 1)
                .gt(Schedule::getRemainingQuota, 0)
                .ge(Schedule::getScheduleDate, LocalDate.now())
                .orderByAsc(Schedule::getScheduleDate);
        return list(wrapper);
    }

    @Override
    public PageResult<Schedule> listPage(int page, int size, Long packageId, Long farmhouseId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        if (packageId != null) {
            wrapper.eq(Schedule::getPackageId, packageId);
        }
        if (farmhouseId != null) {
            wrapper.eq(Schedule::getFarmhouseId, farmhouseId);
        }
        wrapper.orderByAsc(Schedule::getScheduleDate);

        Page<Schedule> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public boolean decreaseQuota(Long id, int count) {
        // 原子性减少剩余配额，若配额不足则更新行数为0
        LambdaUpdateWrapper<Schedule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Schedule::getId, id)
                .ge(Schedule::getRemainingQuota, count)
                .setSql("remaining_quota = remaining_quota - " + count);
        return update(wrapper);
    }
}
