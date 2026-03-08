package com.farmstay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDTO {
    private Long reservationId;

    @NotNull(message = "农家乐ID不能为空")
    private Long farmhouseId;

    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    @NotNull(message = "人数不能为空")
    private Integer personCount;

    private String paymentMethod;
    private String remark;
}
