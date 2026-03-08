package com.farmstay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long reservationId;
    private Long farmhouseId;
    private Long packageId;
    private String farmhouseName;
    private String packageName;
    private Integer personCount;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private Integer status;
    private String paymentMethod;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime completeTime;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
