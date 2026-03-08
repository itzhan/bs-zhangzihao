package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.AnnouncementDTO;
import com.farmstay.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {

    Announcement create(AnnouncementDTO dto);

    Announcement update(Long id, AnnouncementDTO dto);

    void delete(Long id);

    Announcement getById(Long id);

    PageResult<Announcement> listPublished(int page, int size);

    PageResult<Announcement> listPage(int page, int size, Integer type, Integer status);
}
