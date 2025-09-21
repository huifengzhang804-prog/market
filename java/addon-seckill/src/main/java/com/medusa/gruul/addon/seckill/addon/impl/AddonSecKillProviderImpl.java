package com.medusa.gruul.addon.seckill.addon.impl;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.addon.seckill.addon.AddonSecKillProvider;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillActivityDao;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillProductDao;
import com.medusa.gruul.addon.seckill.util.SeckillUtil;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.activity.ActivityTimeAndPrice;
import com.medusa.gruul.common.model.activity.SimpleActivity;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * @author WuDi date: 2022/11/22
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonSecKillProviderImpl implements AddonSecKillProvider {

    private final SeckillActivityDao seckillActivityDao;
    private final SeckillProductDao seckillProductDao;


    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activityBudget")
    public ActivityResp seckillBudget(ActivityParam param) {
        return activity(param);
    }

    /**
     * 获取秒杀活动信息
     *
     * @param activityParam 活动参数
     * @return 秒杀活动信息
     */
    @Override
    @Log("获取秒杀信息")
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activity")
    public ActivityResp activity(ActivityParam activityParam) {

        Map<ShopProductSkuKey, Integer> skuKeyMap = activityParam.getSkuKeyMap();
        if (MapUtil.isEmpty(skuKeyMap)) {
            throw SystemCode.PARAM_MISS.exception();
        }
        ShopProductSkuKey shopProductSkuKey = skuKeyMap.entrySet().iterator().next().getKey();
        Long activityId = activityParam.getActivityId();
        ActivityTimeAndPrice timeAndPrice = SeckillUtil.getTimeAndPrice(
                shopProductSkuKey.getShopId(),
                activityId,
                shopProductSkuKey.getProductId(),
                seckillActivityDao,
                seckillProductDao
        );
        SimpleActivity activity = timeAndPrice.getActivity();
        if (activity == null) {
            throw SystemCode.DATA_NOT_EXIST.dataEx("活动已结束");
        }
        RangeDateTime range = activity.getRange();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(range.getStart())) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("活动未开始");
        }
        if (now.isAfter(range.getEnd())) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("活动已结束");
        }
        Map<Long, Long> prices = timeAndPrice.getPrice();
        Long price;
        if (MapUtil.isEmpty(prices) || (price = prices.get(shopProductSkuKey.getSkuId())) == null) {
            throw SystemCode.DATA_NOT_EXIST.dataEx("该商品未参加此次活动");
        }
        return new ActivityResp()
                .setStackable(activity.getStackable())
                .setSkuKeyPriceMap(Map.of(
                        shopProductSkuKey,
                        price
                ));

    }

    /**
     * 获取秒杀活动商品详情
     *
     * @return 秒杀活动商品详情
     */
    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "activity")
    public ActivityDetailVO activity(Long activityId, Long userId, ShopProductSkuKey key) {
        ActivityTimeAndPrice timeAndPrice = SeckillUtil.getTimeAndPrice(
                key.getShopId(),
                activityId,
                key.getProductId(),
                seckillActivityDao,
                seckillProductDao
        );
        if (timeAndPrice == null) {
            return null;
        }
        SimpleActivity simpleActivity = timeAndPrice.getActivity();
        Map<Long, Long> price = timeAndPrice.getPrice();
        return new ActivityDetailVO()
                .setType(OrderType.SPIKE)
                .setActivityId(activityId)
                .setActivityPrice(price.get(key.getSkuId()))
                .setTime(simpleActivity.getRange())
                .setStackable(simpleActivity.getStackable());
    }


}


