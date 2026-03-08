package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.entity.Notification;

public interface NotificationService extends IService<Notification> {

    void send(Long userId, String title, String content, Integer type);

    PageResult<Notification> listByUserId(Long userId, int page, int size);

    void markRead(Long id);

    void markAllRead(Long userId);

    long getUnreadCount(Long userId);
}
