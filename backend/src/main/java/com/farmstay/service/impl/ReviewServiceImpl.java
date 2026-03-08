package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.ReviewDTO;
import com.farmstay.dto.ReviewReplyDTO;
import com.farmstay.entity.Review;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.ReviewMapper;
import com.farmstay.service.FarmhouseService;
import com.farmstay.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private FarmhouseService farmhouseService;

    @Override
    public Review create(Long userId, ReviewDTO dto) {
        Review review = new Review();
        review.setUserId(userId);
        review.setFarmhouseId(dto.getFarmhouseId());
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setStatus(0); // 待审核
        save(review);
        return review;
    }

    @Override
    public PageResult<Review> listApprovedByFarmhouseId(Long farmhouseId, int page, int size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getFarmhouseId, farmhouseId)
                .eq(Review::getStatus, 1) // 只查已审核通过
                .orderByDesc(Review::getCreateTime);

        Page<Review> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Review> listPage(int page, int size, Integer status, Long farmhouseId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }
        if (farmhouseId != null) {
            wrapper.eq(Review::getFarmhouseId, farmhouseId);
        }
        wrapper.orderByDesc(Review::getCreateTime);

        Page<Review> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public void approve(Long id) {
        Review review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setStatus(1); // 审核通过
        updateById(review);

        // 重新计算农家乐评分
        farmhouseService.updateRating(review.getFarmhouseId());
    }

    @Override
    public void reject(Long id) {
        Review review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setStatus(2); // 审核拒绝
        updateById(review);
    }

    @Override
    public void reply(Long id, ReviewReplyDTO dto) {
        Review review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setAdminReply(dto.getAdminReply());
        review.setReplyTime(LocalDateTime.now());
        updateById(review);
    }
}
