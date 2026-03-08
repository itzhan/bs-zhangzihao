package com.farmstay.controller;

import com.farmstay.common.Result;
import com.farmstay.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String module) {
        return Result.success(auditLogService.listPage(page, size, action, module));
    }
}
