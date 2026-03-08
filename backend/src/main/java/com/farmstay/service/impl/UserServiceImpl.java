package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.LoginRequest;
import com.farmstay.dto.RegisterRequest;
import com.farmstay.dto.UserDTO;
import com.farmstay.entity.User;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.UserMapper;
import com.farmstay.service.UserService;
import com.farmstay.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User register(RegisterRequest req) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setGender(req.getGender());
        user.setRole("USER");
        user.setStatus(1);
        save(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<String, Object> login(LoginRequest req) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        user.setPassword(null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateProfile(Long id, UserDTO dto) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public void updatePassword(Long id, String oldPwd, String newPwd) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        updateById(user);
    }

    @Override
    public PageResult<User> listPage(int page, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = page(new Page<>(page, size), wrapper);
        // 隐藏密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public void toggleStatus(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        updateById(user);
    }
}
