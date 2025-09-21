package com.medusa.gruul.overview.service.modules.base.service.impl;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.overview.service.model.OverviewConstant;
import com.medusa.gruul.overview.service.modules.base.service.OverviewBaseService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShop;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewUser;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewShopService;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewUserService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author 张治保
 * date 2022/11/23
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OverviewBaseServiceImpl implements OverviewBaseService {

    private final ShopRpcService shopRpcService;
    private final IOverviewShopService overviewShopService;
    private final IOverviewUserService overviewUserService;
    private OverviewBaseService overviewBaseService;

    @Override
    public Option<OverviewShop> getShopById(Long shopId) {
        return Option.of(
                RedisUtil.getCacheMap(
                        OverviewShop.class,
                        () -> overviewShopService.lambdaQuery().eq(OverviewShop::getShopId, shopId).one(),
                        Duration.ofHours(CommonPool.NUMBER_TWO),
                        OverviewConstant.OVERVIEW_SHOP_CACHE_KEY,
                        shopId
                )
        );
    }

    @Override
    public void checkOrSaveShop(Long shopId) {
        if (shopId == null || shopId.equals(0L) || getShopById(shopId).isDefined()) {
            return;
        }
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        if (shopInfo == null) {
            return;
        }
        overviewBaseService.saveShop(shopId, shopInfo.getName(), shopInfo.getLogo());
    }

    @Override
    @Redisson(value = OverviewConstant.OVERVIEW_UPDATE_SHOP_LOCK_KEY, key = "#shopInfo.id")
    public void updateShopInfo(ShopInfoVO shopInfo) {
        Long shopId = shopInfo.getId();
        if (getShopById(shopId).isEmpty()) {
            overviewShopService.save(
                    new OverviewShop()
                            .setShopId(shopId)
                            .setShopName(shopInfo.getName())
                            .setShopLogo(shopInfo.getLogo())
            );
            return;
        }
        overviewShopService.lambdaUpdate()
                .set(OverviewShop::getShopName, shopInfo.getName())
                .set(OverviewShop::getShopLogo, shopInfo.getLogo())
                .eq(OverviewShop::getShopId, shopId)
                .update();
    }

    @Override
    public void checkOrSaveShop(Long shopId, String shopName, String shopLogo) {
        if (getShopById(shopId).isDefined()) {
            return;
        }
        overviewBaseService.saveShop(shopId, shopName, shopLogo);
    }

    @Override
    @Redisson(value = OverviewConstant.OVERVIEW_UPDATE_SHOP_LOCK_KEY, key = "#shopId")
    public OverviewShop saveShop(Long shopId, String shopName, String shopLogo) {
        return getShopById(shopId)
                .getOrElse(() -> {
                    OverviewShop overviewShop = new OverviewShop()
                            .setShopId(shopId)
                            .setShopName(shopName)
                            .setShopLogo(shopLogo);
                    boolean success = overviewShopService.save(overviewShop);
                    if (!success) {
                        log.error("保存分销商信息失败");
                    }
                    return overviewShop;
                });

    }


    private Option<OverviewUser> getConsumer(Long userId) {
        return Option.of(
                RedisUtil.getCacheMap(
                        OverviewUser.class,
                        () -> overviewUserService.lambdaQuery().eq(OverviewUser::getUserId, userId).one(),
                        Duration.ofHours(CommonPool.NUMBER_TWO),
                        OverviewConstant.OVERVIEW_CONSUMER_CACHE_KEY,
                        userId
                )
        );
    }


    @Override
    @Redisson(value = OverviewConstant.OVERVIEW_SAVE_CONSUMER_LOCK_KEY, key = "#userId")
    public void checkOrSaveConsumer(Long userId, String name, String avatar) {
        if (getConsumer(userId).isDefined()) {
            return;
        }
        boolean success = overviewUserService.save(
                new OverviewUser()
                        .setUserId(userId)
                        .setName(name)
                        .setAvatar(avatar)
        );
        if (!success) {
            log.error("保存用户信息失败:{}:{}:{}", userId, name, avatar);
        }
    }


    /**
     * 为了走动态代理
     */
    @Autowired
    public void setOverviewBaseService(OverviewBaseService overviewBaseService) {
        this.overviewBaseService = overviewBaseService;
    }
}
