package com.medusa.gruul.addon.platform.model;

/**
 * @author 张治保
 * @since 2024/2/22
 */
public interface PlatformConstant {

    /**
     * 平台缓存前缀
     */
    String PLATFORM_ADDON_KEY = "addon:platform:cache";

    /**
     * 装修缓存前缀
     */
    String DECORATION_KEY = PLATFORM_ADDON_KEY + ":decoration";

    /**
     * 装修页面缓存前缀
     */
    String DECORATION_PAGE_KEY = DECORATION_KEY + ":page";
    /**
     * 装修页面副本数缓存前缀
     */
    String DECORATION_PAGE_COPY_KEY = DECORATION_PAGE_KEY + ":copy";

    /**
     * 装修模板副本数缓存前缀
     */
    String DECORATION_TEMPLATE_COPY_KEY = DECORATION_KEY + ":template:copy";
    /**
     * WEB配置参数缓存key
     */
    String WEB_PARAM_CONFIG_KEY = PLATFORM_ADDON_KEY + ":webParamConfig:module";

    /**
     * 闪屏广告缓存key
     */
    String SPLASH_AD_KEY = PLATFORM_ADDON_KEY + ":splashAd";
    /**
     * 闪屏广告使用信息缓存key
     * 活动时间只弹一次
     */
    String SPLASH_AD_USE_INFO_ONCE_KEY = SPLASH_AD_KEY + ":useInfo:once";
    /**
     * 闪屏广告使用信息缓存key
     * 活动时间每日弹多次
     */
    String SPLASH_AD_USE_INFO_DAY_MULTI_KEY = SPLASH_AD_KEY + ":useInfo:multi";
    /**
     * 首页弹框缓存key
     */
    String  HOME_PAGE_WIN_KEY= PLATFORM_ADDON_KEY + ":homePageWin";
}
