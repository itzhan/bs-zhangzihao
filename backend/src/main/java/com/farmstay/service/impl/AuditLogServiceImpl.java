package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.entity.AuditLog;
import com.farmstay.mapper.AuditLogMapper;
import com.farmstay.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    public void log(Long userId, String username, String action, String module, String description, String ip) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setModule(module);
        auditLog.setDescription(description);
        auditLog.setIp(ip);
        save(auditLog);
    }

    @Override
    public PageResult<AuditLog> listPage(int page, int size, String action, String module) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(action)) {
            wrapper.eq(AuditLog::getAction, action);
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(AuditLog::getModule, module);
        }
        wrapper.orderByDesc(AuditLog::getCreateTime);

        Page<AuditLog> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
