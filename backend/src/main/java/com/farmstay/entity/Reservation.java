package com.farmstay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String reservationNo;
    private Long userId;
    private Long farmhouseId;
    private Long packageId;
    private Long scheduleId;
    private LocalDate reserveDate;
    private Integer personCount;
    private String contactName;
    private String contactPhone;
    private String remark;
    private Integer status;
    private String cancelReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
