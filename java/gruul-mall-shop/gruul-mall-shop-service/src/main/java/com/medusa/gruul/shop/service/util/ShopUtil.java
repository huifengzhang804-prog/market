package com.medusa.gruul.shop.service.util;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.shop.api.constant.ShopConstant;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;

/**
 * shop util
 *
 * @author xiaoq
 * @since 2022-08-18 14:36
 */
public interface ShopUtil {

    /**
     * 店铺装修页面缓存 key
     *
     * @param shopId       店铺id
     * @param endpointType 客户端终端类型
     * @param pageTypeEnum 页面类型
     * @param customerType 自定义类型 可以为空
     * @return key
     */
    static String pageCacheKey(Long shopId, DecorationEndpointTypeEnum endpointType, PageTypeEnum pageTypeEnum, String customerType) {
        Object[] keys = {ShopConstant.DECORATION_PAGE_CACHE_KEY, shopId, endpointType, pageTypeEnum, customerType};
        if (StrUtil.isEmpty(customerType)) {
            keys = new Object[]{ShopConstant.DECORATION_PAGE_CACHE_KEY, shopId, endpointType, pageTypeEnum};
        }
        return RedisUtil.key(keys);
    }

    /**
     * 店铺装修页面缓存 key 匹配模式
     *
     * @param shopId       店铺id
     * @param endpointType 客户端终端类型
     * @return key pattern
     */
    static String pageCacheKeyPattern(Long shopId, DecorationEndpointTypeEnum endpointType) {
        return RedisUtil.key(ShopConstant.DECORATION_PAGE_CACHE_KEY, shopId, endpointType, "*");
    }

    /**
     * 装修页面副本数缓存 key
     *
     * @param shopId 店铺id
     * @param id     页面id
     * @return key
     */
    static String pageCopyCacheKey(Long shopId, Long id) {
        return RedisUtil.key(ShopConstant.DECORATION_PAGE_COPY_CACHE_KEY, shopId, id);
    }


    /**
     * 装修模板副本📚缓存 key
     *
     * @param shopId 店铺id
     * @param id     模板id
     * @return key
     */
    static String templateCopyCacheKey(Long shopId, Long id) {
        return RedisUtil.key(ShopConstant.DECORATION_TEMPLATE_COPY_KEY, shopId, id);
    }


    /**
     * 计算两个经纬度坐标之间的距离
     *
     * @param lon1 第一个点的经度
     * @param lat1 第一个点的纬度
     * @param lon2 第二个点的经度
     * @param lat2 第二个点的纬度
     * @return 两点之间的距离（单位：千米）
     */
    static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        return RedisUtil.distance(
                        new Point(lon1, lat1),
                        new Point(lon2, lat2)
                ).in(RedisGeoCommands.DistanceUnit.KILOMETERS)
                .getValue();
    }

    /**
     * 渲染副本名称
     *
     * @param sourceName 原名称
     * @param count      当前副本数
     * @return 副本名称
     */
    static String copyName(String sourceName, Number count) {
        return sourceName + "_副本" + count;
    }

    /**
     * 根据店铺模式获取装修业务类型
     *
     * @param shopMode 店铺运营模式
     * @return 装修业务类型
     */
    static TemplateBusinessTypeEnum decorationBusiness(ShopMode shopMode) {
        return switch (shopMode) {
            case O2O -> TemplateBusinessTypeEnum.O2O;
            case COMMON -> TemplateBusinessTypeEnum.ONLINE;
            default -> throw ShopError.SHOP_DECORATE_DATA_NOT_EXIST.exception();
        };
    }

    /**
     * 复制的装修描述 i18n
     *
     * @return 复制的装修描述
     */
    static String copiedDesc() {
        return I18N.msg("copied from platform");
    }

}
