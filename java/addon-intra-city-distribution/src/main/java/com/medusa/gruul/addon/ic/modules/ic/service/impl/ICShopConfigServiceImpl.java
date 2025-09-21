package com.medusa.gruul.addon.ic.modules.ic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.ic.model.ICConstant;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopConfigDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.BillType;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.ShopConfigVO;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopConfigDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopConfigService;
import com.medusa.gruul.addon.ic.modules.uupt.util.UuptHelper;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.helper.ShopGeo;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.vividsolutions.jts.geom.Point;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Service
@RequiredArgsConstructor
public class ICShopConfigServiceImpl implements ICShopConfigService {

    private final ShopRpcService shopRpcService;
    private final ICShopConfigDao shopConfigDao;

    private static void getShowMsg(ICShopConfig icShopConfig, ShopIcDistributeInfoDTO distributeInfo) {
        //存在免配送费限制
        if (Objects.nonNull(icShopConfig.getFreeLimit())) {
            distributeInfo.setShowMsg("满" + icShopConfig.getFreeLimit() + "免配送费");
            return;
        }
        if (BillType.WEIGHT.equals(icShopConfig.getBillMethod().getType())) {
            //按重量算配送配 只考虑基础配送费
            if (BigDecimal.valueOf(0).equals(icShopConfig.getBaseDelivery())) {
                distributeInfo.setShowMsg("免配送费");
            } else {
                distributeInfo.setShowMsg("配送费￥" + icShopConfig.getBaseDelivery() / 10000);
            }
        } else {

            Long totalPrice = icShopConfig.getBaseDelivery() +
                    icShopConfig.getBillMethod().billMethodPrice(distributeInfo.getDistance());
            if (totalPrice.equals(0L)) {
                distributeInfo.setShowMsg("免配送费");
            } else {
                distributeInfo.setShowMsg("配送费￥" + totalPrice / 10000);
            }

        }
    }

    @Override
    public ICShopConfig config(Long shopId) {
        return RedisUtil.getCacheMap(
                ICShopConfig.class,
                () -> shopConfigDao.lambdaQuery()
                        .eq(ICShopConfig::getShopId, shopId)
                        .one(),
                Duration.ofDays(7),
                shopConfCacheKey(shopId)
        );
    }

    @Override
    public ShopConfigVO getConfig(Long shopId) {
        ICShopConfig config = config(shopId);
        ShopAddressVO shopAddress = shopRpcService.shopAddress(Set.of(shopId)).get(shopId);
        return ShopConfigVO.of(shopAddress, config == null ? new ICShopConfig() : config);
    }

    private String shopConfCacheKey(Long shopId) {
        return RedisUtil.key(ICConstant.SHOP_CONFIG_CACHE_KEY, shopId);
    }

    @Override
    @Redisson(name = ICConstant.SHOP_CONFIG_CACHE_SAVE_LOCK_KEY, key = "#shopId")
    public void saveConfig(Long shopId, ICShopConfigDTO param) {
        //如果允许开放平台配送 但是uupt 未开通 则提示
        //只有开启同城配送才校验
        if (param.getIcStatus() && param.getEnableOpen() && StrUtil.isEmpty(UuptHelper.getOpenid(shopId))) {
            throw ICError.UUPT_UNACTIVATED.exception();
        }
        RedisUtil.doubleDeletion(
                () -> {
                    Long id = shopConfigDao.lambdaQuery()
                            .select(ICShopConfig::getId)
                            .eq(ICShopConfig::getShopId, shopId)
                            .oneOpt()
                            .map(BaseEntity::getId)
                            .orElse(null);
                    if (param.getIcStatus()) {
                        //开启同城配送
                        if (id == null) {
                            ICShopConfig config = param.toConfig(shopId);
                            shopConfigDao.save(config);
                            return;
                        }
                        shopConfigDao.lambdaUpdate()
                                .set(ICShopConfig::getIcStatus, Boolean.TRUE)
                                .set(ICShopConfig::getEnableSelf, param.getEnableSelf())
                                .set(ICShopConfig::getEnableOpen, param.getEnableOpen())
                                .set(ICShopConfig::getDefaultType, param.getDefaultType())
                                .set(ICShopConfig::getWarmBox, param.getWarmBox())
                                .set(ICShopConfig::getDeliveryRange, param.getDeliveryRange())
                                .set(ICShopConfig::getDescription, param.getDescription())
                                .set(ICShopConfig::getMinLimit, param.getMinLimit())
                                .set(ICShopConfig::getBaseDelivery, param.getBaseDelivery())
                                .set(ICShopConfig::getBillMethod, JSON.toJSONString(param.getBillMethod()))
                                .set(ICShopConfig::getFreeLimit, param.getFreeLimit())
                                .eq(ICShopConfig::getShopId, shopId)
                                .eq(BaseEntity::getId, id)
                                .update();
                    } else {
                        //关闭同城配送 只有在以前有数据的时候才更新 没有数据 more就是关闭的
                        if (Objects.nonNull(id)) {
                            shopConfigDao.lambdaUpdate()
                                    .set(ICShopConfig::getIcStatus, Boolean.FALSE)
                                    .eq(ICShopConfig::getShopId, shopId)
                                    .eq(BaseEntity::getId, id)
                                    .update();
                        }

                    }
                },
                shopConfCacheKey(shopId)
        );
    }

    @Override
    public Map<Long, ShopIcDistributeInfoDTO> calculateDistributeInfo(Set<Long> shopIds, Point point) {

        //查询店铺配置
        Map<Long, ICShopConfig> shopConfigMap = shopConfigDao.lambdaQuery()
                .select(
                        ICShopConfig::getShopId,
                        ICShopConfig::getDeliveryRange,
                        ICShopConfig::getMinLimit,
                        ICShopConfig::getBaseDelivery,
                        ICShopConfig::getBillMethod,
                        ICShopConfig::getFreeLimit,
                        ICShopConfig::getIcStatus
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

        Map<Long, ShopIcDistributeInfoDTO> result = Maps.newHashMapWithExpectedSize(shopConfigMap.size());
        //距离计费
        //到店铺的距离
        for (Long shopId : shopIds) {
            ICShopConfig icShopConfig = shopConfigMap.get(shopId);
            if (Objects.isNull(icShopConfig) || !icShopConfig.getIcStatus()) {
                //没有配置或者没有开启同城配送
                continue;
            }
            BigDecimal distance = BigDecimal.valueOf(
                    ShopGeo.distance(RedisUtil.getRedisTemplate(), point, shopId)
                            //以公里为单位
                            .in(RedisGeoCommands.DistanceUnit.KILOMETERS)
                            .getValue()
            );
            ShopIcDistributeInfoDTO distributeInfo = new ShopIcDistributeInfoDTO();
            //店铺id
            distributeInfo.setShopId(shopId);
            //起送金额
            distributeInfo.setMinLimit(icShopConfig.getMinLimit());
            distributeInfo.setDistance(distance);
            if (distance.compareTo(icShopConfig.getDeliveryRange()) > 0) {
                //超出配送范围
                distributeInfo.setInDeliveryRange(Boolean.FALSE);
                result.put(shopId, distributeInfo);
                continue;
            }
            //在配送范围内
            distributeInfo.setInDeliveryRange(Boolean.TRUE);
            getShowMsg(icShopConfig, distributeInfo);
            result.put(shopId, distributeInfo);
        }
        return result;
    }


}
