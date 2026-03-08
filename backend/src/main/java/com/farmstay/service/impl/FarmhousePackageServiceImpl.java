package com.farmstay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmstay.common.PageResult;
import com.farmstay.dto.PackageDTO;
import com.farmstay.entity.FarmhousePackage;
import com.farmstay.exception.BusinessException;
import com.farmstay.mapper.FarmhousePackageMapper;
import com.farmstay.service.FarmhousePackageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmhousePackageServiceImpl extends ServiceImpl<FarmhousePackageMapper, FarmhousePackage> implements FarmhousePackageService {

    @Override
    public FarmhousePackage create(PackageDTO dto) {
        FarmhousePackage pkg = new FarmhousePackage();
        pkg.setFarmhouseId(dto.getFarmhouseId());
        pkg.setName(dto.getName());
        pkg.setDescription(dto.getDescription());
        pkg.setPrice(dto.getPrice());
        pkg.setOriginalPrice(dto.getOriginalPrice());
        pkg.setCoverImage(dto.getCoverImage());
        pkg.setType(dto.getType());
        pkg.setCapacity(dto.getCapacity());
        pkg.setDuration(dto.getDuration());
        pkg.setIncludes(dto.getIncludes());
        pkg.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        pkg.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        save(pkg);
        return pkg;
    }

    @Override
    public FarmhousePackage update(Long id, PackageDTO dto) {
        FarmhousePackage pkg = getById(id);
        if (dto.getFarmhouseId() != null) pkg.setFarmhouseId(dto.getFarmhouseId());
        if (dto.getName() != null) pkg.setName(dto.getName());
        if (dto.getDescription() != null) pkg.setDescription(dto.getDescription());
        if (dto.getPrice() != null) pkg.setPrice(dto.getPrice());
        if (dto.getOriginalPrice() != null) pkg.setOriginalPrice(dto.getOriginalPrice());
        if (dto.getCoverImage() != null) pkg.setCoverImage(dto.getCoverImage());
        if (dto.getType() != null) pkg.setType(dto.getType());
        if (dto.getCapacity() != null) pkg.setCapacity(dto.getCapacity());
        if (dto.getDuration() != null) pkg.setDuration(dto.getDuration());
        if (dto.getIncludes() != null) pkg.setIncludes(dto.getIncludes());
        if (dto.getSortOrder() != null) pkg.setSortOrder(dto.getSortOrder());
        if (dto.getStatus() != null) pkg.setStatus(dto.getStatus());
        updateById(pkg);
        return pkg;
    }

    @Override
    public void delete(Long id) {
        FarmhousePackage pkg = getById(id);
        if (pkg == null) {
            throw new BusinessException("套餐不存在");
        }
        removeById(id);
    }

    @Override
    public FarmhousePackage getById(Long id) {
        FarmhousePackage pkg = super.getById(id);
        if (pkg == null) {
            throw new BusinessException("套餐不存在");
        }
        return pkg;
    }

    @Override
    public List<FarmhousePackage> listByFarmhouseId(Long farmhouseId) {
        LambdaQueryWrapper<FarmhousePackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmhousePackage::getFarmhouseId, farmhouseId)
                .eq(FarmhousePackage::getStatus, 1)
                .orderByAsc(FarmhousePackage::getSortOrder);
        return list(wrapper);
    }

    @Override
    public PageResult<FarmhousePackage> listPage(int page, int size, Long farmhouseId, Integer type, Integer status) {
        LambdaQueryWrapper<FarmhousePackage> wrapper = new LambdaQueryWrapper<>();
        if (farmhouseId != null) {
            wrapper.eq(FarmhousePackage::getFarmhouseId, farmhouseId);
        }
        if (type != null) {
            wrapper.eq(FarmhousePackage::getType, type);
        }
        if (status != null) {
            wrapper.eq(FarmhousePackage::getStatus, status);
        }
        wrapper.orderByAsc(FarmhousePackage::getSortOrder).orderByDesc(FarmhousePackage::getCreateTime);

        Page<FarmhousePackage> result = page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
