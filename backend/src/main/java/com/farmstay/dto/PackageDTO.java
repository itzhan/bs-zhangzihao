package com.farmstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackageDTO {
    private Long id;

    @NotNull(message = "农家乐ID不能为空")
    private Long farmhouseId;

    @NotBlank(message = "套餐名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private String coverImage;

    @NotNull(message = "套餐类型不能为空")
    private Integer type;

    private Integer capacity;
    private String duration;
    private String includes;
    private Integer sortOrder;
    private Integer status;
}
