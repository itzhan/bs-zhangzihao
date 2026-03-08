package com.farmstay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farmhouse")
public class Farmhouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String shortDesc;
    private String address;
    private String phone;
    private String coverImage;
    private String images;
    private String ownerName;
    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String tags;
    private String features;
    private String businessHours;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
