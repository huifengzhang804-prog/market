package com.medusa.gruul.addon.ic.addon.impl;

import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.addon.ic.addon.IcAddonProvider;
import com.medusa.gruul.addon.ic.model.ICConstant;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.BillMethod;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.BillType;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.ShopConfigVO;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopConfigDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopConfigService;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopOrderService;
import com.medusa.gruul.addon.ic.modules.ic.strategy.ICDeliverStrategyFactory;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.ShopFreightParam;
import com.medusa.gruul.order.api.addon.freight.SkuInfoParam;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.shop.api.helper.ShopGeo;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.vividsolutions.jts.geom.Point;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @since 2023/3/6
 */
@Slf4j
@AddonProviders
@DubboService
@Service
@RequiredArgsConstructor
public class IcAddonProviderImpl implements IcAddonProvider {

    private final ICShopConfigDao shopConfigDao;
    private final ICShopConfigService shopConfigService;
    private final ICShopOrderService shopOrderService;
    private final ICDeliverStrategyFactory deliverStrategyFactory;

    /**
     * 计算配送费用
     * todo 可优化  优化为批量redis查询 查询不到的数据再从数据库查询
     *
     * @param platformFreight 运费计算信息
     * @return 计算结果 key 店铺 id , value 配送费 单位元
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "distributionCost", filter = FILTER)
    public Map<String, BigDecimal> getCalculateCost(PlatformFreightParam platformFreight) {
        Point location = platformFreight.getLocation();
        List<ShopFreightParam> shopFreights = platformFreight.getShopFreights();
        Set<Long> shopIds = shopFreights.stream().map(ShopFreightParam::getShopId).collect(Collectors.toSet());
        //查询店铺配置
        Map<Long, ICShopConfig> shopConfigMap = TenantShop.disable(() -> {
            return shopConfigDao.lambdaQuery()
                    .select(
                            ICShopConfig::getShopId,
                            ICShopConfig::getDeliveryRange,
                            ICShopConfig::getMinLimit,
                            ICShopConfig::getBaseDelivery,
                            ICShopConfig::getBillMethod,
                            ICShopConfig::getFreeLimit
                    )
                    .in(ICShopConfig::getShopId, shopIds)
                    .list()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    ICShopConfig::getShopId,
                                    Function.identity()
                            )
                    );

        });

        //收集结果
        Map<String, BigDecimal> result = new HashMap<>(shopFreights.size());

        for (ShopFreightParam shopFreight : shopFreights) {
            Long shopId = shopFreight.getShopId();
            ICShopConfig config = shopConfigMap.get(shopId);
            if (config == null) {
                throw ICError.IC_UNACTIVATED.dataEx(shopId);
            }
            List<SkuInfoParam> skuInfo = shopFreight.getFreights().stream()
                    .flatMap(v -> v.getSkuInfos().stream())
                    .toList();
            //免费额度 大于这个额度免运费
            Long freeLimit = config.getFreeLimit();
            BigDecimal weight = BigDecimal.ZERO;
            long totalPrice = 0;
            for (SkuInfoParam sku : skuInfo) {
                Integer num = sku.getNum();
                weight = weight.add(sku.getWeight().multiply(BigDecimal.valueOf(num)));
                totalPrice += (sku.getPrice().longValue() * num);
            }
            //是否达到了起送费
            if (totalPrice < config.getMinLimit()) {
                throw ICError.BELOW_MIN_LIMIT.exception();
            }
            //距离计费
            //到店铺的距离
            BigDecimal distance = BigDecimal.valueOf(
                    ShopGeo.distance(RedisUtil.getRedisTemplate(), location, shopId)
                            //以公里为单位
                            .in(RedisGeoCommands.DistanceUnit.KILOMETERS)
                            .getValue()
            );
            if (distance.compareTo(config.getDeliveryRange()) > 0) {
                throw ICError.OUT_OF_RANGE.exception();
            }
            //免费
            if (freeLimit != null && totalPrice >= freeLimit) {
                result.put(shopId.toString(), BigDecimal.ZERO);
                continue;
            }
            BillMethod billMethod = config.getBillMethod();
            //配送费 初始化未基础配送费
            Long deliveryPrice = config.getBaseDelivery()
                    //基础配送费 + 计费方式计算出的配送费
                    + billMethod.billMethodPrice(BillType.WEIGHT == billMethod.getType() ? weight : distance);
            result.put(shopId.toString(), AmountCalculateHelper.toYuan(deliveryPrice));

        }
        return result;
    }

    /**
     * 获取店铺的起送金额
     *
     * @param shopIds 店铺ids
     * @return 获取店铺的起送金额
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = "shopSigningCategory", methodName = "shopInitialDeliveryCharge")
    public Map<Long, BigDecimal> getShopInitialDeliveryCharge(Set<Long> shopIds) {
        return shopConfigDao.lambdaQuery()
                .select(ICShopConfig::getShopId, ICShopConfig::getMinLimit)
                .in(ICShopConfig::getShopId, shopIds)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                ICShopConfig::getShopId,
                                v -> AmountCalculateHelper.toYuan(v.getMinLimit())
                        )
                );
//        for (Long shopId : shopIds) {
//            if (result.containsKey(shopId)) {
//                continue;
//            }
//            throw ICError.IC_UNACTIVATED.dataEx(shopId);
//        }

    }

    @Override
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#param.orderNo")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "icOrderReport")
    public void icOrderReport(Boolean isSelf, ICOrder param) {
        Long shopId = param.getShopId();
        ICShopConfig config = shopConfigService.config(shopId);
        if (config == null) {
            throw ICError.IC_UNACTIVATED.dataEx(shopId);
        }
        if (!config.getEnableSelf() && !config.getEnableOpen()) {
            throw ICError.IC_UNACTIVATED.dataEx(shopId);
        }
        if (isSelf && !config.getEnableSelf()) {
            throw ICError.IC_UNACTIVATED.dataEx(shopId);
        }
        if (!isSelf && !config.getEnableOpen()) {
            throw ICError.IC_UNACTIVATED.dataEx(shopId);
        }
        param.setWarmBox(config.getWarmBox());
        deliverStrategyFactory.execute(isSelf ? ICDeliveryType.SELF : ICDeliveryType.UUPT, param);
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "checkAllSupportIc")
    public boolean checkAllSupportIc(Set<Long> shopIds) {
        for (Long shopId : shopIds) {
            ShopConfigVO config = shopConfigService.getConfig(shopId);
            if (BooleanUtil.isFalse(config.getIcStatus())) {
                //只要有一个店铺不支持同城配送 则返回False
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "icOrderStatus")
    public Map<String, ICStatus> icOrderStatus(Set<String> orderNos, Boolean handledErrorToPending) {
        return shopOrderService.icOrderStatus(orderNos, handledErrorToPending);
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "queryShopIcStatus")
    public Boolean queryShopIcStatus(Long shopId) {
        ShopConfigVO config = shopConfigService.getConfig(shopId);
        return BooleanUtil.isTrue(config.getIcStatus());

    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_CART, supporterId = "cartAddonSupporter", methodName = "queryShopIcStatusForCart")
    public Boolean queryShopIcStatusForCart(Long shopId) {
        ShopConfigVO config = shopConfigService.getConfig(shopId);
        return BooleanUtil.isTrue(config.getIcStatus());
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = "shopSigningCategory", methodName = "getShopIcDistributeInfo")
    public Map<Long, ShopIcDistributeInfoDTO> getShopIcDistributeInfo(Set<Long> shopIds, Point point) {
        Map<Long, ShopIcDistributeInfoDTO> result = shopConfigService.calculateDistributeInfo(shopIds, point);
        return result;
    }
}
