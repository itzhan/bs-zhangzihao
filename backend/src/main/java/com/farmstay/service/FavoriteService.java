package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {

    void add(Long userId, Long farmhouseId);

    void remove(Long userId, Long farmhouseId);

    PageResult<Favorite> listByUserId(Long userId, int page, int size);

    boolean isFavorited(Long userId, Long farmhouseId);
}
