package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.FarmhouseDTO;
import com.farmstay.entity.Farmhouse;
import com.farmstay.entity.Review;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.FarmhouseMapper;
import com.farmstay.mapper.ReviewMapper;
import com.farmstay.service.FarmhouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FarmhouseServiceImpl extends ServiceImpl<FarmhouseMapper, Farmhouse> implements FarmhouseService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Farmhouse create(FarmhouseDTO dto) {
        Farmhouse farmhouse = new Farmhouse();
        farmhouse.setName(dto.getName());
        farmhouse.setDescription(dto.getDescription());
        farmhouse.setShortDesc(dto.getShortDesc());
        farmhouse.setAddress(dto.getAddress());
        farmhouse.setPhone(dto.getPhone());
        farmhouse.setCoverImage(dto.getCoverImage());
        farmhouse.setImages(dto.getImages());
        farmhouse.setOwnerName(dto.getOwnerName());
        farmhouse.setTags(dto.getTags());
        farmhouse.setFeatures(dto.getFeatures());
        farmhouse.setBusinessHours(dto.getBusinessHours());
        farmhouse.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        farmhouse.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        farmhouse.setRating(BigDecimal.ZERO);
        farmhouse.setReviewCount(0);
        save(farmhouse);
        return farmhouse;
    }

    @Override
    public Farmhouse update(Long id, FarmhouseDTO dto) {
        Farmhouse farmhouse = getById(id);
        if (dto.getName() != null) farmhouse.setName(dto.getName());
        if (dto.getDescription() != null) farmhouse.setDescription(dto.getDescription());
        if (dto.getShortDesc() != null) farmhouse.setShortDesc(dto.getShortDesc());
        if (dto.getAddress() != null) farmhouse.setAddress(dto.getAddress());
        if (dto.getPhone() != null) farmhouse.setPhone(dto.getPhone());
        if (dto.getCoverImage() != null) farmhouse.setCoverImage(dto.getCoverImage());
        if (dto.getImages() != null) farmhouse.setImages(dto.getImages());
        if (dto.getOwnerName() != null) farmhouse.setOwnerName(dto.getOwnerName());
        if (dto.getTags() != null) farmhouse.setTags(dto.getTags());
        if (dto.getFeatures() != null) farmhouse.setFeatures(dto.getFeatures());
        if (dto.getBusinessHours() != null) farmhouse.setBusinessHours(dto.getBusinessHours());
        if (dto.getSortOrder() != null) farmhouse.setSortOrder(dto.getSortOrder());
        if (dto.getStatus() != null) farmhouse.setStatus(dto.getStatus());
        updateById(farmhouse);
        return farmhouse;
    }

    @Override
    public void delete(Long id) {
        Farmhouse farmhouse = getById(id);
        if (farmhouse == null) {
            throw new BusinessException("农家乐不存在");
        }
        removeById(id);
    }

    @Override
    public Farmhouse getById(Long id) {
        Farmhouse farmhouse = super.getById(id);
        if (farmhouse == null) {
            throw new BusinessException("农家乐不存在");
        }
        return farmhouse;
    }

    @Override
    public PageResult<Farmhouse> listPage(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<Farmhouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Farmhouse::getName, keyword)
                    .or().like(Farmhouse::getTags, keyword));
        }
        if (status != null) {
            wrapper.eq(Farmhouse::getStatus, status);
        }
        wrapper.orderByAsc(Farmhouse::getSortOrder).orderByDesc(Farmhouse::getCreateTime);

        Page<Farmhouse> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public List<Farmhouse> listActive(String keyword) {
        LambdaQueryWrapper<Farmhouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Farmhouse::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Farmhouse::getName, keyword)
                    .or().like(Farmhouse::getTags, keyword));
        }
        wrapper.orderByAsc(Farmhouse::getSortOrder).orderByDesc(Farmhouse::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void toggleStatus(Long id) {
        Farmhouse farmhouse = getById(id);
        farmhouse.setStatus(farmhouse.getStatus() == 1 ? 0 : 1);
        updateById(farmhouse);
    }

    @Override
    public void updateRating(Long farmhouseId) {
        // 查询该农家乐所有已审核通过的评价
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getFarmhouseId, farmhouseId)
                .eq(Review::getStatus, 1);
        List<Review> reviews = reviewMapper.selectList(wrapper);

        Farmhouse farmhouse = super.getById(farmhouseId);
        if (farmhouse == null) return;

        if (reviews.isEmpty()) {
            farmhouse.setRating(BigDecimal.ZERO);
            farmhouse.setReviewCount(0);
        } else {
            double avg = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            farmhouse.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            farmhouse.setReviewCount(reviews.size());
        }
        updateById(farmhouse);
    }
}
