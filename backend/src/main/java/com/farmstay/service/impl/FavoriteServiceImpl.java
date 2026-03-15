package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.entity.Farmhouse;
import com.farmstay.entity.Favorite;
import com.farmstay.mapper.FavoriteMapper;
import com.farmstay.service.FarmhouseService;
import com.farmstay.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    @Lazy
    private FarmhouseService farmhouseService;

    @Override
    public void add(Long userId, Long farmhouseId) {
        if (isFavorited(userId, farmhouseId)) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setFarmhouseId(farmhouseId);
        save(favorite);
    }

    @Override
    public void remove(Long userId, Long farmhouseId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getFarmhouseId, farmhouseId);
        remove(wrapper);
    }

    @Override
    public PageResult<Favorite> listByUserId(Long userId, int page, int size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Farmhouse> listFarmhousesByUserId(Long userId, int page, int size) {
        // 先查收藏分页
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);
        Page<Favorite> favPage = page(new Page<>(page, size), wrapper);

        if (favPage.getRecords().isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L, page, size);
        }

        // 批量查农家乐详情
        List<Long> farmhouseIds = favPage.getRecords().stream()
                .map(Favorite::getFarmhouseId)
                .collect(Collectors.toList());
        List<Farmhouse> farmhouses = farmhouseService.listByIds(farmhouseIds);

        // 保持收藏顺序
        List<Farmhouse> ordered = farmhouseIds.stream()
                .map(id -> farmhouses.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null))
                .filter(f -> f != null)
                .collect(Collectors.toList());

        return new PageResult<>(ordered, favPage.getTotal(), page, size);
    }

    @Override
    public boolean isFavorited(Long userId, Long farmhouseId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getFarmhouseId, farmhouseId);
        return count(wrapper) > 0;
    }
}
