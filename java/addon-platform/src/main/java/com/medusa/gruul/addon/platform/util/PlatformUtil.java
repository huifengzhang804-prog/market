package com.medusa.gruul.addon.platform.util;

import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.addon.platform.model.PlatformConstant;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import java.time.LocalDateTime;
import jodd.util.StringUtil;

/**
 * Description 平台工具类
 *
 * @author xiaoq
 * @since 2022-11-02 18:51
 */
public interface PlatformUtil {

    /**
     * 装修页面缓存 key
     *
     * @param endpointType 客户端终端类型
     * @param pageTypeEnum 页面类型
     * @param customType   自定义类型 可为空
     * @return key
     */
    static String pageCacheKey(DecorationEndpointTypeEnum endpointType, PageTypeEnum pageTypeEnum, String customType) {
        Object[] keys =
                StringUtil.isEmpty(customType) ?
                        new Object[]{PlatformConstant.DECORATION_PAGE_KEY, endpointType, pageTypeEnum}
                        : new Object[]{PlatformConstant.DECORATION_PAGE_KEY, endpointType, pageTypeEnum, customType};
        return RedisUtil.key(keys);
    }


    /**
     * 装修页面缓存 key 匹配模式
     *
     * @param endpointType 客户端终端类型
     * @return key pattern
     */
    static String pageCacheKeyPattern(DecorationEndpointTypeEnum endpointType) {
        return RedisUtil.key(PlatformConstant.DECORATION_PAGE_KEY, endpointType, "*");
    }

    /**
     * 装修页面副本数缓存 key
     *
     * @param id 页面id
     * @return key
     */
    static String pageCopyCacheKey(Long id) {
        return RedisUtil.key(PlatformConstant.DECORATION_PAGE_COPY_KEY, id);
    }

    /**
     * 装修模板副本📚缓存 key
     *
     * @param id 模板id
     * @return key
     */
    static String templateCopyCacheKey(Long id) {
        return RedisUtil.key(PlatformConstant.DECORATION_TEMPLATE_COPY_KEY, id);
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
     * web 配置模块缓存key
     * @param module
     * @return
     */
    static String paramConfigModuleKey(WebParamModuleEnum module) {
        return RedisUtil.key(PlatformConstant.WEB_PARAM_CONFIG_KEY, module.getValue());

    }

    /**
     * splash广告缓存key
     * @param endPointTypeEnum
     * @return
     */
    static String splashAdKey(DecorationEndpointTypeEnum endPointTypeEnum) {
        return RedisUtil.key(PlatformConstant.SPLASH_AD_KEY,endPointTypeEnum.name());
    }

    /**
     * 活动期间每个用户只弹一次
     * @param endPointTypeEnum
     * @return
     */
    static String splashAdUseInfoOnceKey(DecorationEndpointTypeEnum endPointTypeEnum) {
        return RedisUtil.key(PlatformConstant.SPLASH_AD_USE_INFO_ONCE_KEY,endPointTypeEnum.name());
    }

    /**
     * 每日弹多次
     * @param endPointTypeEnum
     * @return
     */
    static String splashAdUseInfoDayMultiKey(DecorationEndpointTypeEnum endPointTypeEnum) {
        String format = DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd");
        return RedisUtil.key(PlatformConstant.SPLASH_AD_USE_INFO_DAY_MULTI_KEY,format,endPointTypeEnum.name());
    }

    static String homePageWinCacheKey(DecorationEndpointTypeEnum endPoint) {
        return RedisUtil.key(PlatformConstant.HOME_PAGE_WIN_KEY,endPoint.name());
    }
}
