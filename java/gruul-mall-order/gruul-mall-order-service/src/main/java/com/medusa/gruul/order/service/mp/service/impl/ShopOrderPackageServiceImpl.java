package com.medusa.gruul.order.service.mp.service.impl;

import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;
import com.medusa.gruul.order.service.mp.mapper.ShopOrderPackageMapper;
import com.medusa.gruul.order.service.mp.service.IShopOrderPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * 订单物流配送 服务实现类
 *
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class ShopOrderPackageServiceImpl extends ServiceImpl<ShopOrderPackageMapper, ShopOrderPackage> implements IShopOrderPackageService {

    @Override
    public List<ShopOrderPackage> deliveredPackages(String orderNo, ShopOrderPackageQueryBO query) {
        return baseMapper.deliveredPackages(orderNo, query);
    }
}
