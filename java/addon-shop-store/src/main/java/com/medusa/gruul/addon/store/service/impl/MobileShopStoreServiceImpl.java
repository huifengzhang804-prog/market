package com.medusa.gruul.addon.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.store.model.dto.ShopStoreDistanceDTO;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.addon.store.model.vo.ShopStoreDistanceVO;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.service.IShopAssistantService;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.addon.store.service.MobileShopStoreService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.vividsolutions.jts.geom.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author xiaoq
 * @Description 移动店铺门店服务实现层
 * @date 2023-03-10 16:16
 */
@Service
@RequiredArgsConstructor
public class MobileShopStoreServiceImpl implements MobileShopStoreService {
    /**
     * 地球半径，单位km
     */
    private static final double EARTH_RADIUS = 6371.0;
    private final IShopStoreService shopStoreService;
    private final IShopAssistantService shopAssistantService;

    /**
     * 计算俩点之间距离
     *
     * @param storePoint 店铺定点
     * @param userPoint  用户定点
     * @return 距离
     */
    private static double calculateDistance(Point storePoint, Point userPoint) {
        double radLon1 = Math.toRadians(storePoint.getX());
        double radLat1 = Math.toRadians(storePoint.getY());
        double radLon2 = Math.toRadians(userPoint.getX());
        double radLat2 = Math.toRadians(userPoint.getY());
        // 计算两点纬度之差和经度之差的弧度值
        double dLat = radLat2 - radLat1;
        double dLon = radLon2 - radLon1;
        // 使用Haversine公式计算球面距离
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * 根据店员手机号获取所在门店信息
     *
     * @param shopAssistantPhone 门店店员手机号
     * @return 门店信息
     */
    @Override
    public ShopStore getShopStoreByShopAssistantPhone(String shopAssistantPhone) {
        ShopAssistant shopAssistant = shopAssistantService.lambdaQuery().eq(ShopAssistant::getAssistantPhone, shopAssistantPhone).one();
        if (shopAssistant == null) {
            throw new GlobalException("该账号(手机号)不是店员");
        }
        ShopStore shopStore = shopStoreService.lambdaQuery().select(
                        BaseEntity::getId,
                        ShopStore::getStoreName,
                        ShopStore::getShopName,
                        ShopStore::getShopId,
                        ShopStore::getStoreImg,
                        ShopStore::getBusinessEndTime,
                        ShopStore::getBusinessStartTime,
                        ShopStore::getDetailedAddress,
                        ShopStore::getStartDeliveryDay,
                        ShopStore::getEndDeliveryDay,
                        ShopStore::getFunctionaryName,
                        ShopStore::getFunctionaryPhone,
                        ShopStore::getStoreLogo
                )
                .eq(BaseEntity::getId, shopAssistant.getStoreId())
                .eq(ShopStore::getStatus, StoreStatus.NORMAL)
                .one();
        if (shopStore == null) {
            throw new GlobalException(SystemCode.SENTINEL_AUTHORITY_BLOCK_CODE, "当前门店不存在,或门店异常");
        }
        return shopStore;
    }

    /**
     * 获取店铺门店信息 并进行距离排序
     *
     * @param shopStoreDistance 门店店铺距离
     */
    @Override
    public List<ShopStoreDistanceVO> getStoreDistance(ShopStoreDistanceDTO shopStoreDistance) {
        List<ShopStore> shopStoreList = shopStoreService.lambdaQuery().select(
                        BaseEntity::getId,
                        ShopStore::getStoreName,
                        ShopStore::getStoreImg,
                        ShopStore::getDetailedAddress,
                        ShopStore::getFunctionaryPhone,
                        ShopStore::getLocation)
                .eq(ShopStore::getStatus, StoreStatus.NORMAL)
                .eq(ShopStore::getShopId, shopStoreDistance.getShopId())
                .list();
        if (CollUtil.isEmpty(shopStoreList)) {
            throw new GlobalException("当前店铺无可用门店,请稍后再试");
        }
        // 组装按距离返回数据
        Stream<ShopStoreDistanceVO> shopStoreDistanceVOStream = shopStoreList.stream()
                .map(shopStore -> {
                    ShopStoreDistanceVO shopStoreDistanceVO = new ShopStoreDistanceVO()
                            .setId(shopStore.getId())
                            .setShopId(shopStore.getShopId())
                            .setStoreName(shopStore.getStoreName())
                            .setDetailedAddress(shopStore.getDetailedAddress())
                            .setFunctionaryPhone(shopStore.getFunctionaryPhone());
                    if (Objects.nonNull(shopStoreDistance.getPoint())) {
                        double distance = calculateDistance(shopStore.getLocation(), shopStoreDistance.getPoint());
                        shopStoreDistanceVO.setDistance(distance);
                    }
                    return shopStoreDistanceVO;
                });
        if (Objects.nonNull(shopStoreDistance.getPoint())) {
            shopStoreDistanceVOStream = shopStoreDistanceVOStream.sorted(Comparator.comparing(ShopStoreDistanceVO::getDistance));
        }
        return shopStoreDistanceVOStream.toList();
    }

    @Override
    public ShopStore getStoreOptionalDeliveryTime(Long id, Long shopId) {

        return shopStoreService.lambdaQuery()
                .select(
                        ShopStore::getBusinessStartTime,
                        ShopStore::getBusinessEndTime,
                        ShopStore::getStartDeliveryDay,
                        ShopStore::getEndDeliveryDay
                )
                .eq(BaseEntity::getId, id)
                .eq(ShopStore::getShopId, shopId)
                .one();
    }

}
