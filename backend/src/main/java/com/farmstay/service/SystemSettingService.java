package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.entity.SystemSetting;

import java.util.List;
import java.util.Map;

public interface SystemSettingService extends IService<SystemSetting> {

    String getByKey(String key);

    List<SystemSetting> getAll();

    void update(String key, String value);

    void batchUpdate(Map<String, String> settings);
}
