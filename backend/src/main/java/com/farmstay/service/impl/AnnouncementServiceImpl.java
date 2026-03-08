package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.AnnouncementDTO;
import com.farmstay.entity.Announcement;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.AnnouncementMapper;
import com.farmstay.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Announcement create(AnnouncementDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType());
        announcement.setCoverImage(dto.getCoverImage());
        announcement.setStartTime(dto.getStartTime());
        announcement.setEndTime(dto.getEndTime());
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        announcement.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        save(announcement);
        return announcement;
    }

    @Override
    public Announcement update(Long id, AnnouncementDTO dto) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        if (dto.getTitle() != null) announcement.setTitle(dto.getTitle());
        if (dto.getContent() != null) announcement.setContent(dto.getContent());
        if (dto.getType() != null) announcement.setType(dto.getType());
        if (dto.getCoverImage() != null) announcement.setCoverImage(dto.getCoverImage());
        if (dto.getStartTime() != null) announcement.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) announcement.setEndTime(dto.getEndTime());
        if (dto.getStatus() != null) announcement.setStatus(dto.getStatus());
        if (dto.getSortOrder() != null) announcement.setSortOrder(dto.getSortOrder());
        updateById(announcement);
        return announcement;
    }

    @Override
    public void delete(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        removeById(id);
    }

    @Override
    public Announcement getById(Long id) {
        Announcement announcement = super.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }

    @Override
    public PageResult<Announcement> listPublished(int page, int size) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime);

        Page<Announcement> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Announcement> listPage(int page, int size, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByAsc(Announcement::getSortOrder).orderByDesc(Announcement::getCreateTime);

        Page<Announcement> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
