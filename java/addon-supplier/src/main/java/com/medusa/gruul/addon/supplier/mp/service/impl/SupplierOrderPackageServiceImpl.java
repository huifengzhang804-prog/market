package com.medusa.gruul.addon.supplier.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.supplier.model.dto.OrderMatchQueryDTO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierOrderPackageMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderPackageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Service
public class SupplierOrderPackageServiceImpl extends ServiceImpl<SupplierOrderPackageMapper, SupplierOrderPackage> implements ISupplierOrderPackageService {
    @Override
    public List<SupplierOrderPackage> deliveryPackages(OrderMatchQueryDTO deliveryQuery) {
        return baseMapper.deliveryPackages(deliveryQuery);
    }
}
