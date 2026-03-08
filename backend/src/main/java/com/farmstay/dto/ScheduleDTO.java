package com.farmstay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScheduleDTO {
    private Long id;

    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    @NotNull(message = "农家乐ID不能为空")
    private Long farmhouseId;

    @NotNull(message = "排期日期不能为空")
    private LocalDate scheduleDate;

    @NotNull(message = "总配额不能为空")
    private Integer totalQuota;

    private Integer remainingQuota;
    private BigDecimal priceOverride;
    private Integer status;
}
