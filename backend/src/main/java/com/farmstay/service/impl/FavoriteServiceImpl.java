package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.entity.Favorite;
import com.farmstay.mapper.FavoriteMapper;
import com.farmstay.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    public void add(Long userId, Long farmhouseId) {
        // 若已收藏则不重复添加
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
    public boolean isFavorited(Long userId, Long farmhouseId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getFarmhouseId, farmhouseId);
        return count(wrapper) > 0;
    }
}
