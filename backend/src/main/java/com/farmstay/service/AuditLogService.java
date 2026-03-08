package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {

    void log(Long userId, String username, String action, String module, String description, String ip);

    PageResult<AuditLog> listPage(int page, int size, String action, String module);
}
