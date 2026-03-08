package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.FarmhouseDTO;
import com.farmstay.entity.Farmhouse;

import java.util.List;

public interface FarmhouseService extends IService<Farmhouse> {

    Farmhouse create(FarmhouseDTO dto);

    Farmhouse update(Long id, FarmhouseDTO dto);

    void delete(Long id);

    Farmhouse getById(Long id);

    PageResult<Farmhouse> listPage(int page, int size, String keyword, Integer status);

    List<Farmhouse> listActive(String keyword);

    void toggleStatus(Long id);

    void updateRating(Long farmhouseId);
}
