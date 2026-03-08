package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ReviewDTO;
import com.farmstay.dto.ReviewReplyDTO;
import com.farmstay.entity.Review;

public interface ReviewService extends IService<Review> {

    Review create(Long userId, ReviewDTO dto);

    PageResult<Review> listApprovedByFarmhouseId(Long farmhouseId, int page, int size);

    PageResult<Review> listPage(int page, int size, Integer status, Long farmhouseId);

    void approve(Long id);

    void reject(Long id);

    void reply(Long id, ReviewReplyDTO dto);
}
