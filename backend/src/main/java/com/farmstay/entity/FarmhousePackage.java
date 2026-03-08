package com.farmstay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farmhouse_package")
public class FarmhousePackage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long farmhouseId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String coverImage;
    private Integer type;
    private Integer capacity;
    private String duration;
    private String includes;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
