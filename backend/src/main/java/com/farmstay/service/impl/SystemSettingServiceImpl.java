package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.entity.SystemSetting;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.SystemSettingMapper;
import com.farmstay.service.SystemSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements SystemSettingService {

    @Override
    public String getByKey(String key) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getSettingKey, key);
        SystemSetting setting = getOne(wrapper);
        return setting != null ? setting.getSettingValue() : null;
    }

    @Override
    public List<SystemSetting> getAll() {
        return list();
    }

    @Override
    public void update(String key, String value) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getSettingKey, key);
        SystemSetting setting = getOne(wrapper);
        if (setting == null) {
            throw new BusinessException("系统配置不存在: " + key);
        }
        setting.setSettingValue(value);
        updateById(setting);
    }

    @Override
    @Transactional
    public void batchUpdate(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemSetting::getSettingKey, entry.getKey());
            SystemSetting setting = getOne(wrapper);
            if (setting != null) {
                setting.setSettingValue(entry.getValue());
                updateById(setting);
            } else {
                // 不存在则新建
                SystemSetting newSetting = new SystemSetting();
                newSetting.setSettingKey(entry.getKey());
                newSetting.setSettingValue(entry.getValue());
                save(newSetting);
            }
        }
    }
}
