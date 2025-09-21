package com.medusa.gruul.carrier.pigeon.service.im.service.impl;

import com.medusa.gruul.carrier.pigeon.service.im.entity.ShopInfo;
import com.medusa.gruul.carrier.pigeon.service.im.repository.ShopInfoRepository;
import com.medusa.gruul.carrier.pigeon.service.im.service.IShopService;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopInfoService implements IShopService {

    private final ShopInfoRepository shopInfoRepository;
    private final ShopRpcService shopRpcService;

    /**
     * 尝试从Redis中获取店铺信息,若不存在则从RPC拉取并缓存到Redis.
     *
     * @param shopId 店铺ID
     * @return {@link ShopInfo}
     */
    @Override
    public ShopInfo checkAndGetMessageShop(Long shopId) {
        return Option.of(
                shopInfoRepository.getShopInfoById(shopId)
        ).getOrElse(
                () -> Option.of(shopRpcService.getShopInfoByShopId(shopId))
                        .map(
                                shop -> {
                                    if (ShopStatus.NORMAL != shop.getStatus()) {
                                        throw new GlobalException(String.format("店铺 = {%d}不可用", shopId));
                                    }
                                    ShopInfo shopInfo = new ShopInfo(String.valueOf(shop.getId()), shop.getName(), shop.getLogo());
                                    shopInfoRepository.saveShopInfo(shopInfo);
                                    return shopInfo;
                                }
                        )
                        .getOrElseThrow(() -> new GlobalException(String.format("店铺 = {%d}信息不存在", shopId)))
        );
    }
}
