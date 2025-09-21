package com.medusa.gruul.shop.service.service.rpc;


import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.entity.ShopBankAccount;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopLogisticsAddressVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.shop.service.mp.service.IShopBankAccountService;
import com.medusa.gruul.shop.service.mp.service.IShopLogisticsAddressService;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.medusa.gruul.shop.service.service.ShopInfoService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author wudi
 */

@Service
@DubboService
@RequiredArgsConstructor
public class ShopRpcServiceImpl implements ShopRpcService {

    private final IShopLogisticsAddressService logisticsAddressService;

    private final IShopService shopService;
    private final IShopBankAccountService shopBankAccountService;
    private ShopInfoService shopInfoService;

    @Lazy
    @Autowired
    public void setShopInfoService(ShopInfoService shopInfoService) {
        this.shopInfoService = shopInfoService;
    }

    @Override
    public Map<Long, ShopAddressVO> shopAddress(Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return Map.of();
        }
        if (shopIds.size() == 1) {
            Long shopId = shopIds.iterator().next();
            ShopAddressVO address = this.shopInfoService.getShopById(shopId)
                    .map(shop -> new ShopAddressVO()
                            .setLocation(shop.getLocation())
                            .setArea(shop.getArea())
                            .setAddress(shop.getAddress())
                    )
                    .getOrNull();

            return address == null ? Map.of() : Map.of(shopId, address);
        }

        return shopService.lambdaQuery()
                .select(BaseEntity::getId, Shop::getLocation, Shop::getArea, Shop::getAddress)
                .in(BaseEntity::getId, shopIds)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                BaseEntity::getId,
                                shop -> new ShopAddressVO()
                                        .setLocation(shop.getLocation())
                                        .setArea(shop.getArea())
                                        .setAddress(shop.getAddress())
                        )
                );
    }

    /**
     * 根据店铺id查询店铺基本信息
     *
     * @param shopId 店铺id
     * @return 店铺基本信息
     */
    @Override
    public ShopInfoVO getShopInfoByShopId(Long shopId) {
        return this.shopInfoService.getShopById(shopId)
                .map(ShopInfoVO::fromShop)
                .getOrNull();
    }

    /**
     * 根据店铺id集合批量获取店铺基本信息
     *
     * @param shopIds 店铺id集合
     * @return 店铺基本信息
     */
    @Override
    public List<ShopInfoVO> getShopInfoByShopIdList(Set<Long> shopIds) {
        return this.shopService.listByIds(shopIds)
                .stream()
                .map(ShopInfoVO::fromShop)
                .collect(Collectors.toList());
    }


    @Override
    public Option<Shop> getShopAndShopBankInfo(Long shopId) {
        return shopInfoService.getShopById(shopId)
                .peek(shop -> shop
                        .setLocation(null)
                        .setBankAccount(
                                shopBankAccountService.lambdaQuery()
                                        .eq(ShopBankAccount::getShopId, shopId)
                                        .one()
                        ));
    }

    /**
     * 获取 默认的发货地址/退货地址
     *
     * @param shopId 店铺id
     * @param isSend 是否是收货地址
     * @return 发货地址/收货地址
     */
    @Override
    public ShopLogisticsAddressVO getSendOrReceiveAddress(Long shopId, Boolean isSend) {
        ShopLogisticsAddress logisticsAddress = TenantShop.disable(
                () -> logisticsAddressService.lambdaQuery()
                        .eq(ShopLogisticsAddress::getShopId, shopId)
                        .orderByDesc(isSend ? ShopLogisticsAddress::getDefSend : ShopLogisticsAddress::getDefReceive)
                        .last(SqlHelper.SQL_LIMIT_1)
                        .one()
        );
        if (logisticsAddress == null) {
            return null;
        }
        return new ShopLogisticsAddressVO()
                .setContactName(logisticsAddress.getContactName())
                .setContactPhone(logisticsAddress.getContactPhone())
                .setZipCode(logisticsAddress.getZipCode())
                .setArea(logisticsAddress.getArea())
                .setAddress(logisticsAddress.getAddress());
    }


}
