package com.farmstay.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer status;
    private String role;
}
