package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.LoginRequest;
import com.farmstay.dto.RegisterRequest;
import com.farmstay.dto.UserDTO;
import com.farmstay.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    User register(RegisterRequest req);

    Map<String, Object> login(LoginRequest req);

    User getUserById(Long id);

    User updateProfile(Long id, UserDTO dto);

    void updatePassword(Long id, String oldPwd, String newPwd);

    PageResult<User> listPage(int page, int size, String keyword);

    void toggleStatus(Long id);
}
