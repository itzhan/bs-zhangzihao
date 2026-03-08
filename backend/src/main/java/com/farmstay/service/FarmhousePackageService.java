package com.farmstay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmstay.common.PageResult;
import com.farmstay.dto.PackageDTO;
import com.farmstay.entity.FarmhousePackage;

import java.util.List;

public interface FarmhousePackageService extends IService<FarmhousePackage> {

    FarmhousePackage create(PackageDTO dto);

    FarmhousePackage update(Long id, PackageDTO dto);

    void delete(Long id);

    FarmhousePackage getById(Long id);

    List<FarmhousePackage> listByFarmhouseId(Long farmhouseId);

    PageResult<FarmhousePackage> listPage(int page, int size, Long farmhouseId, Integer type, Integer status);
}
