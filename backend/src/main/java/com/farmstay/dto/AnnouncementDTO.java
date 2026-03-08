package com.farmstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;

    @NotBlank(message = "公告标题不能为空")
    private String title;

    private String content;

    @NotNull(message = "公告类型不能为空")
    private Integer type;

    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Integer sortOrder;
}
