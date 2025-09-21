package com.medusa.gruul.addon.supplier.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.supplier.model.dto.OrderMatchQueryDTO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;

import java.util.List;

/**
 * @author 张治保
 * date 2023/7/25
 */
public interface ISupplierOrderPackageService extends IService<SupplierOrderPackage> {

    /**
     * 查询已发货包裹信息
     *
     * @param deliveryQuery 查询条件
     * @return 已发货包裹信息
     */
    List<SupplierOrderPackage> deliveryPackages(OrderMatchQueryDTO deliveryQuery);
}
