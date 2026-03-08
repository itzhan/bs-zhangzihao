package com.farmstay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTO {
    @NotNull(message = "农家乐ID不能为空")
    private Long farmhouseId;

    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    @NotNull(message = "排期ID不能为空")
    private Long scheduleId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate reserveDate;

    @NotNull(message = "人数不能为空")
    private Integer personCount;

    private String contactName;
    private String contactPhone;
    private String remark;
}
