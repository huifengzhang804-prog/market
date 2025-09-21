package com.medusa.gruul.order.service.modules.deliver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;
import com.medusa.gruul.order.service.modules.deliver.service.ShopOrderDeliverCallShopRpcService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 张治保
 * date 2022/7/26
 * 依赖循环问题，把用到shopRpc的方法单独放到一个新类里面
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShopOrderDeliverCallShopRpcServiceImpl implements ShopOrderDeliverCallShopRpcService {

    private final IShopOrderPackageService shopOrderPackageService;
    private final ShopRpcService shopRpcService;


    @Override
    public List<ShopOrderPackage> deliveredPackages(DeliveryQueryBO deliveryMatch) {
        ShopOrderPackageQueryBO query = new ShopOrderPackageQueryBO()
                .setShopOrderNo(deliveryMatch.getShopOrderNo())
                .setShopId(deliveryMatch.getShopId())
                .setSellType(deliveryMatch.getSellType())
                .setSupplierId(deliveryMatch.getSupplierId());
        ISecurity.match()
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));
        List<ShopOrderPackage> shopOrderPackageList = TenantShop.disable(
                () -> shopOrderPackageService.deliveredPackages(deliveryMatch.getOrderNo(), query));
        if(CollUtil.isEmpty(shopOrderPackageList)){
            return new ArrayList<>();
        }
        List<ShopInfoVO> shopInfoVOList = shopRpcService.getShopInfoByShopIdList(
                shopOrderPackageList.stream().flatMap(p -> Stream.of(p.getDeliverShopId(), p.getShopId())).collect(Collectors.toSet())
        );
        Map<Long, String> shopNameMap = shopInfoVOList.stream().collect(
                Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName)
        );
        return shopOrderPackageList.stream().peek(shopOrderPackage -> shopOrderPackage.setDeliverShopName(
                shopNameMap.getOrDefault(shopOrderPackage.getDeliverShopId() == null
                        ? shopOrderPackage.getShopId()
                        : shopOrderPackage.getDeliverShopId(), OrderConstant.PLATFORM_NAME)
        )).collect(Collectors.toList());
    }
}