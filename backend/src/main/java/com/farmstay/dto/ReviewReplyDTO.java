package com.farmstay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewReplyDTO {
    @NotBlank(message = "管理员回复不能为空")
    private String adminReply;
}
