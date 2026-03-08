package com.farmstay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FarmhouseDTO {
    private Long id;

    @NotBlank(message = "农家乐名称不能为空")
    private String name;

    private String description;
    private String shortDesc;
    private String address;
    private String phone;
    private String coverImage;
    private String images;
    private String ownerName;
    private String tags;
    private String features;
    private String businessHours;
    private Integer sortOrder;
    private Integer status;
}
