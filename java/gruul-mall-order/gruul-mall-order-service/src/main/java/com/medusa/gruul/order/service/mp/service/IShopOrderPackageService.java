package com.medusa.gruul.order.service.mp.service;

import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;

import java.util.List;

/**
 * 
 * 订单物流配送 服务类
 * 
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface IShopOrderPackageService extends IService<ShopOrderPackage> {

    /**
     * 根据查询条件查询所有的订单包裹
     * @param orderNo 订单号
     * @param query 查询条件
     * @return 订单包裹
     */
    List<ShopOrderPackage> deliveredPackages(String orderNo, ShopOrderPackageQueryBO query);


}
