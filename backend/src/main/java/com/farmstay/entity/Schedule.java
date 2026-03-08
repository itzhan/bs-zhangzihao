package com.farmstay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long packageId;
    private Long farmhouseId;
    private LocalDate scheduleDate;
    private Integer totalQuota;
    private Integer remainingQuota;
    private BigDecimal priceOverride;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
